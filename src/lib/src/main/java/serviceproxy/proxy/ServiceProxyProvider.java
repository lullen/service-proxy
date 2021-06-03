package serviceproxy.proxy;

import serviceproxy.model.Response;

public interface ServiceProxyProvider {
    <T> Response<T> invoke(String app, String method, Object request, Class<T> responseClass)
            throws Exception;
}
