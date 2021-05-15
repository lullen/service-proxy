package serviceproxy.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.protobuf.Message;

import serviceproxy.model.Response;
import serviceproxy.proxy.middleware.ProxyMiddleware;

public class BaseServiceProxy {
    private List<ProxyMiddleware> _middlewares = new ArrayList<ProxyMiddleware>();

    public BaseServiceProxy(Set<ProxyMiddleware> middlewares) {
        _middlewares.addAll(middlewares);
    }

    <T> void runBefore(String appId, String method, Message request, Class<T> responseClass) {
        for (var proxyMiddleware : _middlewares) {
            proxyMiddleware.before(appId, method, request, responseClass);
        }
    }

    <T> void runAfter(String appId, String method, Message request, Response<T> response) {
        for (var proxyMiddleware : _middlewares) {
            proxyMiddleware.after(appId, method, request, response);
        }
    }

    // @Override
    // public <T> Response<T> invoke(String appId, String method, Message request, boolean legacy, Class<T> responseClass)
    //         throws Exception {
    //     if (_settings.type == ProxyType.Dapr && !legacy) {
    //         return new DaprProxy(this).invoke(appId, method, request, legacy, responseClass);
    //     } else {
    //         return new InProcProxy().invoke(appId, method, request, legacy, responseClass);
    //     }
    // }

    // @Override
    // public void publish(String pubsubName, String topic, Message request) throws Exception {
    //     if (_settings.type == ProxyType.Dapr) {
    //         new DaprProxy(this).publish(pubsubName, topic, request);
    //     } else {
    //         new InProcProxy().publish(pubsubName, topic, request);
    //     }
    // }

    // @Override
    // public String getSecret(String secretStoreName, String key) throws Exception {
    //     if (_settings.type == ProxyType.Dapr) {
    //         return new DaprProxy(this).getSecret(secretStoreName, key);
    //     } else {
    //         return new InProcProxy().getSecret(secretStoreName, key);
    //     }
    // }
}
