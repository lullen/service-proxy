package serviceproxy.proxy;

import com.google.protobuf.Message;

public class EventPublisher {
    public static void publish(String pubsubName, String topic, Message request) throws Exception {
        var proxy = BaseServiceProxy.create();
        proxy.publish(pubsubName, topic, request);
    }

}
