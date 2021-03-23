package common.proxy;
import com.google.protobuf.Message;

import common.model.Response;

public interface IServiceProxy {
    <T> Response<T> invoke(String app, String method, Message request, Class<T> responseClass) throws Exception;

    void publish(String topic, Message request) throws Exception;

    String secret(String key) throws Exception;
}
