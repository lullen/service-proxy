package serviceproxy.proxy.middleware;

import com.google.protobuf.Message;

import serviceproxy.model.Response;

public abstract class ProxyMiddleware {
    public abstract <T> void before(String appId, String method, Object request, Class<T> responseClass);
    public abstract <T> void after(String appId, String method, Object request, Response<T> response);
}
