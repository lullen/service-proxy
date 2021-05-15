package serviceproxy.pubsub;

import com.google.protobuf.Message;

import org.springframework.stereotype.Component;

import serviceproxy.proxy.DaprProxy;
import serviceproxy.proxy.InProcProxy;
import serviceproxy.proxy.ProxyType;

@Component
public class EventPublisher {

    private static ProxyType type;
    private InProcProxy inProcProxy;
    private DaprProxy daprProxy;

    // Maybe inject IServiceProxy and InProcProxy instead?
    // IServiceProxy will be Dapr / InProc and InProc will be used for legacy
    public EventPublisher(DaprProxy daprProxy, InProcProxy inProcProxy){
        this.daprProxy = daprProxy;
        this.inProcProxy = inProcProxy;
        
    }

    public static void init(ProxyType type) {
        EventPublisher.type = type;
    }

    public void publish(String pubsubName, String topic, Message request) throws Exception {
        if (EventPublisher.type == ProxyType.Dapr) {
            daprProxy.publish(pubsubName, topic, request);
        } else {
            inProcProxy.publish(pubsubName, topic, request);
        }
    }

}
