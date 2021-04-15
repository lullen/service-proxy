package serviceproxy.proxy;

import com.google.protobuf.Message;

public class EventPublisher {
    private static Boolean _initialized = false;

    public static void init(String pubsubName) {
         BaseServiceProxy.initPubSub(pubsubName);
        _initialized = true;
    }

    public static void publish(String topic, Message request) throws Exception {
        if (!_initialized) {
            throw new Exception("Event publisher not initialized");
        }
        var x = BaseServiceProxy.create();
        x.publish(topic, request);
    }

}
