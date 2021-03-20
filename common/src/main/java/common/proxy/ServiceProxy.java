package common.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Injector;
import com.google.protobuf.Message;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import io.dapr.serializer.DefaultObjectSerializer;
import com.google.protobuf.util.JsonFormat;

public class ServiceProxy implements IServiceProxy {
    private static ProxySettings _settings;

    private ServiceProxy() {
    }

    public static ServiceProxy create() {
        var proxy = new ServiceProxy();
        return proxy;
    }

    public static void init(ProxySettings settings) throws Exception {
        if (settings.type.equals("inproc") && settings.injector == null) {
            throw new Exception("Injector must be specified.");
        }
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
            // TODO Auto-generated method stub

            var json = com.google.protobuf.util.JsonFormat.printer().print(request);
            System.out.println(json);
            var builder = request.newBuilderForType();
            com.google.protobuf.util.JsonFormat.parser().merge(json, builder);
            var req = builder.build();
            var serializedRequest = new DefaultObjectSerializer().serialize(json);

            try (DaprClient client = new DaprClientBuilder().build()) {
                client.publishEvent(_settings.pubsubName, topic, serializedRequest).block();
            }

        }

        @Override
        public Map<String, String> secret(String key) throws Exception {
            try (var client = new DaprClientBuilder().build()) {
                return client.getSecret(_settings.secretStoreName, key).block();
            }
        }
    }

    private class InProxProxy implements IServiceProxy {
        @Override()
        public <T> T invoke(String appId, String method, Message request, Class<T> responseClass) throws Exception {
            var className = method.substring(0, method.lastIndexOf("."));
            var methodName = method.substring(method.lastIndexOf(".") + 1, method.length());

            var invokeClass = Class.forName(appId + ".interfaces." + className);
            var invokeMethod = getMethod(methodName, invokeClass);

            var instance = _settings.injector.getInstance(invokeClass);
            // var parseFrom = invokeMethod.getParameterTypes()[0].getMethod("parseFrom",
            // ByteString.class);
            // var reqq = parseFrom.invoke(null, ((com.google.protobuf.Message)
            // request).toByteString());

            var start = System.currentTimeMillis();
            var response = invokeMethod.invoke(instance, request);
            var end = System.currentTimeMillis();

            System.out.println("Calling " + method + " took: " + (end - start) + "ms");

            // var respParseFrom = responseClass.getMethod("parseFrom", ByteString.class);
            // var respReqq = respParseFrom.invoke(null, ((com.google.protobuf.Message)
            // response).toByteString());

            return (T) response;
        }

        private Method getMethod(String methodName, Class<?> invokeClass) {
            Method invokeMethod = null;

            for (var method : invokeClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    invokeMethod = method;
                    break;
                }
            }
            return invokeMethod;
        }

        @Override
        public void publish(String topic, Message request) throws Exception {
            if (_settings.pubsubName == "") {
                throw new Exception("Property pubsubName is not set");
            }
            System.out.println("InProc: Published event to topic " + topic + " on " + _settings.pubsubName);

        }

        @Override
        public Map<String, String> secret(String key) throws Exception {
            var map = new HashMap<String, String>();
            map.put(key, key + "-secret");
            return map;
        }
    }

    @Override
    public <T> T invoke(String appId, String method, Message request, Class<T> responseClass) throws Exception {
        if (_settings.type == "dapr") {
            // System.out.println("Calling " + method + " using dapr");
            return new DaprProxy().invoke(appId, method, request, responseClass);
        } else {
            return new InProxProxy().invoke(appId, method, request, responseClass);
        }
    }

    @Override
    public void publish(String topic, Message request) throws Exception {
        if (_settings.type == "dapr") {
            // System.out.println("Calling " + method + " using dapr");
            new DaprProxy().publish(topic, request);
        } else {
            new InProxProxy().publish(topic, request);
        }

    }

    @Override
    public Map<String, String> secret(String key) throws Exception {
        if (_settings.type == "dapr") {
            // System.out.println("Calling " + method + " using dapr");
            return new DaprProxy().secret(key);
        } else {
            return new InProxProxy().secret(key);
        }
    }
}
