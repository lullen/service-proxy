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

    <T> void runBefore(String appId, String method, Object request, Class<T> responseClass) {
        for (var proxyMiddleware : _middlewares) {
            proxyMiddleware.before(appId, method, request, responseClass);
        }
    }

    <T> void runAfter(String appId, String method, Object request, Response<T> response) {
        for (var proxyMiddleware : _middlewares) {
            proxyMiddleware.after(appId, method, request, response);
        }
    }
}
