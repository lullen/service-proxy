package common.proxy;

import java.util.Map;

import com.google.protobuf.Message;

public interface IServiceProxy {
    <T> T invoke(String app, String method, Message request, Class<T> responseClass) throws Exception;

    void publish(String topic, Message request) throws Exception;

    Map<String, String> secret(String key) throws Exception;
}
