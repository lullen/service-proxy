package serviceproxy.proxy;
import com.google.protobuf.Message;

import serviceproxy.model.Response;

public interface IServiceProxy {
    <T> Response<T> invoke(String app, String method, Message request, Class<T> responseClass) throws Exception;

    void publish(String topic, Message request) throws Exception;

    String getSecret(String key) throws Exception;
}
