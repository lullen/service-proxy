package serviceproxy.pubsub;

import com.google.protobuf.Message;

import serviceproxy.proxy.BaseServiceProxy;

public class EventPublisher {
    public static void publish(String pubsubName, String topic, Message request) throws Exception {
        var proxy = BaseServiceProxy.create();
        proxy.publish(pubsubName, topic, request);
    }

}
