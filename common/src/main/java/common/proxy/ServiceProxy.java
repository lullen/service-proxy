package common.proxy;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.protobuf.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.helpers.ServiceLoader;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import io.dapr.serializer.DefaultObjectSerializer;

public class ServiceProxy implements IServiceProxy {
    private static final Logger _logger = LogManager.getLogger(ServiceProxy.class);
    private static ProxySettings _settings;

    private ServiceProxy() {
    }

    public static ServiceProxy create() {
        var proxy = new ServiceProxy();
        return proxy;
    }

    public static void init(ProxySettings settings) throws Exception {
        _settings = settings;
    }

    private class DaprProxy implements IServiceProxy {
        @Override()
        public <T> T invoke(String appId, String method, Message request, Class<T> responseClass) throws Exception {
            try (DaprClient client = new DaprClientBuilder().build()) {
                var resp = client.invokeMethod(appId, method, request, HttpExtension.NONE, responseClass).block();
                return (T) resp;
            }
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
        public String secret(String key) throws Exception {
            try (var client = new DaprClientBuilder().build()) {
                return client.getSecret(_settings.secretStoreName, key).block().get(key);
            }
        }
    }

    private class InProxProxy implements IServiceProxy {
        @Override()
        @SuppressWarnings("unchecked")
        public <T> T invoke(String appId, String method, Message request, Class<T> responseClass) throws Exception {
            var instance = ServiceLoader.create(method);
            var invokeMethod = ServiceLoader.getMethod(method, instance.getClass());

            var start = System.currentTimeMillis();
            var response = invokeMethod.invoke(instance, request);
            var end = System.currentTimeMillis();

            _logger.info("Calling " + method + " took: " + (end - start) + "ms");
            if (response.getClass().equals(responseClass)) {
                return (T) response;
            }
            return null;
        }

        @Override
        public void publish(String topic, Message request) throws Exception {
            if (_settings.pubsubName == "") {
                throw new Exception("Property pubsubName is not set");
            }

            _logger.info("InProc: Published event to topic " + topic + " on " + _settings.pubsubName);

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
        public String secret(String key) throws Exception {
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
    public <T> T invoke(String appId, String method, Message request, Class<T> responseClass) throws Exception {
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
    public String secret(String key) throws Exception {
        if (_settings.type == ProxyType.Dapr) {
            return new DaprProxy().secret(key);
        } else {
            return new InProxProxy().secret(key);
        }
    }
}
