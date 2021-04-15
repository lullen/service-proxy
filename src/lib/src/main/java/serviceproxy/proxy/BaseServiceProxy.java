package serviceproxy.proxy;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.protobuf.Message;
import io.grpc.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import serviceproxy.helpers.ServiceLoader;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;
import serviceproxy.proxy.middleware.ProxyMiddleware;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import io.dapr.exceptions.DaprException;
import io.dapr.serializer.DefaultObjectSerializer;

public class BaseServiceProxy implements IServiceProxy {
    private static final Logger _logger = LogManager.getLogger(BaseServiceProxy.class);
    private static ProxySettings _settings = new ProxySettings();
    private static Injector _injector;
    private List<ProxyMiddleware> _middlewares = new ArrayList<ProxyMiddleware>();

    @Inject
    public BaseServiceProxy(Set<ProxyMiddleware> middlewares) {
        _middlewares.addAll(middlewares);
    }

    public static BaseServiceProxy create() {
        var inst = _injector.getInstance(BaseServiceProxy.class);
        return inst;
    }

    public static void initProxy(ProxyType type, Injector injector) {
        _settings.type = type;
        _injector = injector;
    }

    public static void initPubSub(String pubsubName) {
        _settings.pubsubName = pubsubName;
    }

    public static void initSecrets(String secretStore) {
        _settings.secretStoreName = secretStore;
    }

    private <T> void runBefore(String appId, String method, Message request, Class<T> responseClass) {
        for (var proxyMiddleware : _middlewares) {
            proxyMiddleware.before(appId, method, request, responseClass);
        }
    }

    private <T> void runAfter(String appId, String method, Message request, Response<T> response) {
        for (var proxyMiddleware : _middlewares) {
            proxyMiddleware.after(appId, method, request, response);
        }
    }

    private class DaprProxy implements IServiceProxy {
        @Override()
        public <T> Response<T> invoke(String appId, String method, Message request, Class<T> responseClass)
                throws Exception {

            Response<T> methodResult;
            runBefore(appId, method, request, responseClass);

            try (DaprClient client = new DaprClientBuilder().build()) {
                try {
                    var resp = client.invokeMethod(appId, method, request, HttpExtension.NONE, responseClass).block();
                    methodResult = new Response<T>(resp);
                } catch (DaprException exception) {
                    var errorCode = StatusCode.Exception;
                    var code = Status.Code.valueOf(exception.getErrorCode());

                    switch (code) {
                    case ALREADY_EXISTS:
                        errorCode = StatusCode.AlreadyExists;
                        break;
                    case INVALID_ARGUMENT:
                        errorCode = StatusCode.InvalidInput;
                        break;
                    case NOT_FOUND:
                        errorCode = StatusCode.NotFound;
                        break;
                    case UNAUTHENTICATED:
                        errorCode = StatusCode.Unauthorized;
                        break;
                    default:
                        errorCode = StatusCode.Exception;
                        break;
                    }

                    methodResult = new Response<T>();
                    methodResult.error = new Error(errorCode, exception.getMessage());
                }
            }

            runAfter(appId, method, request, methodResult);
            return methodResult;
        }

        @Override
        public void publish(String topic, Message request) throws Exception {
            var json = com.google.protobuf.util.JsonFormat.printer().print(request);
            var serializedRequest = new DefaultObjectSerializer().serialize(json);

            try (DaprClient client = new DaprClientBuilder().build()) {
                client.publishEvent(_settings.pubsubName, topic, serializedRequest).block();
            }
        }

        @Override
        public String getSecret(String key) throws Exception {
            try (var client = new DaprClientBuilder().build()) {
                return client.getSecret(_settings.secretStoreName, key).block().get(key);
            }
        }
    }

    private class InProxProxy implements IServiceProxy {
        @Override()
        public <T> Response<T> invoke(String appId, String method, Message request, Class<T> responseClass)
        throws Exception {
            runBefore(appId, method, request, responseClass);
            
            var instance = ServiceLoader.create(method);
            var invokeMethod = ServiceLoader.getMethod(method, instance.getClass());
            
            @SuppressWarnings("unchecked")
            var response = (Response<T>) invokeMethod.invoke(instance, request);

            runAfter(appId, method, request, response);
            return response;
        }

        @Override
        public void publish(String topic, Message request) throws Exception {
            if (_settings.pubsubName == "") {
                throw new Exception("Property pubsubName is not set");
            }

            _logger.info("InProc: Published event to topic {} on {}", topic, _settings.pubsubName);

            var subscription = ServiceLoader.getSubscriptions().stream().filter(s -> s.topic.equals(topic)).findFirst();
            if (subscription.isPresent()) {
                new Thread() {
                    public void run() {
                        try {
                            var invokeClass = ServiceLoader.create(subscription.get().method);
                            var invokeMethod = ServiceLoader.getMethod(subscription.get().method,
                                    invokeClass.getClass());
                            invokeMethod.invoke(invokeClass, request);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }

        @Override
        public String getSecret(String key) throws Exception {
            var map = new HashMap<String, String>();
            map.put(key, key + "-secret");
            var classLoader = getClass().getClassLoader();

            var a = classLoader.getResource(_settings.secretStoreName).toURI();
            var path = Paths.get(a);
            String json = Files.readString(path);

            var gson = new Gson();
            var jsonObj = gson.fromJson(json, HashMap.class);
            var val = jsonObj.get(key);
            return (String) val;
        }

    }

    @Override
    public <T> Response<T> invoke(String appId, String method, Message request, Class<T> responseClass)
            throws Exception {
        if (_settings.type == ProxyType.Dapr) {
            return new DaprProxy().invoke(appId, method, request, responseClass);
        } else {
            return new InProxProxy().invoke(appId, method, request, responseClass);
        }
    }

    @Override
    public void publish(String topic, Message request) throws Exception {
        if (_settings.type == ProxyType.Dapr) {
            new DaprProxy().publish(topic, request);
        } else {
            new InProxProxy().publish(topic, request);
        }
    }

    @Override
    public String getSecret(String key) throws Exception {
        if (_settings.type == ProxyType.Dapr) {
            return new DaprProxy().getSecret(key);
        } else {
            return new InProxProxy().getSecret(key);
        }
    }
}
