package serviceproxy.proxy;

import com.google.protobuf.Message;

public interface EventPublisherProvider {
    void publish(String pubsubName, String topic, Message request) throws Exception;
}