package common.proxy.middleware;

import com.google.protobuf.Message;

import common.model.Response;

public abstract class ProxyMiddleware {
    public abstract <T> void before(String appId, String method, Message request, Class<T> responseClass);
    public abstract <T> void after(String appId, String method, Message request, Response<T> response);
}
