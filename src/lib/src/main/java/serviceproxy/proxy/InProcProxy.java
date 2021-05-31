package serviceproxy.proxy;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.Gson;
import com.google.protobuf.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import serviceproxy.helpers.ServiceLoader;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;
import serviceproxy.proxy.middleware.ProxyMiddleware;

@Component
public class InProcProxy extends BaseServiceProxy implements IServiceProxy {
    private static final Logger logger = LogManager.getLogger(InProcProxy.class);

    /**
     *
     */
    private ServiceLoader serviceLoader;

    public InProcProxy(Set<ProxyMiddleware> middlewares, ServiceLoader serviceLoader) {
        super(middlewares);
        this.serviceLoader = serviceLoader;
    }

    @Override()
    @SuppressWarnings("unchecked")
    public <T> Response<T> invoke(String appId, String method, Object request, Class<T> responseClass)
            throws Exception {
        this.runBefore(appId, method, request, responseClass);
        Response<T> methodResult;
        try {
            var instance = serviceLoader.create(method);
            if (instance != null) {
                var invokeMethod = serviceLoader.getMethod(method, instance.getClass());
                methodResult = (Response<T>) invokeMethod.invoke(instance, request);
            } else {
                methodResult = new Response<T>();
                var errorMsg = String.format("Could not invoke \"%s.%s\". Have you registered it in the service proxy?",
                        appId, method);
                methodResult.error = new Error(StatusCode.Exception, errorMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            methodResult = new Response<T>();
            methodResult.error = new Error(StatusCode.Exception, e.getMessage());
        }
        this.runAfter(appId, method, request, methodResult);
        return methodResult;
    }

    @Override
    public void publish(String pubsubName, String topic, Message request) throws Exception {
        if (pubsubName == "") {
            throw new Exception("Property pubsubName is not set");
        }
        if (topic == "") {
            throw new Exception("Property topic is not set");
        }

        logger.info("InProc: Published event to topic {} on {}", topic, pubsubName);

        var subscription = ServiceLoader.getSubscriptions().stream().filter(s -> s.topic.equals(topic)).findFirst();
        if (subscription.isPresent()) {
            new Thread() {
                public void run() {
                    try {
                        var invokeClass = serviceLoader.create(subscription.get().method);
                        var invokeMethod = serviceLoader.getMethod(subscription.get().method, invokeClass.getClass());
                        invokeMethod.invoke(invokeClass, request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public String getSecret(String secretStoreName, String key) throws Exception {
        var map = new HashMap<String, String>();
        map.put(key, key + "-secret");
        var classLoader = getClass().getClassLoader();

        String json;
        try {
            var a = classLoader.getResource(secretStoreName + ".json").toURI();
            var path = Paths.get(a);
            json = Files.readString(path);

        } catch (Exception e) {
            throw new Exception("Could not open file " + secretStoreName + ".json", e);
        }

        var gson = new Gson();
        var jsonObj = gson.fromJson(json, HashMap.class);
        var val = jsonObj.get(key);
        return (String) val;
    }

}