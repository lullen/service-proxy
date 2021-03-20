package server.interfaces;

import com.budbee.proto.HelloResponse;

import common.pubsub.Subscriber;
import common.server.ExposedService;
import events.server.HelloEvent;

import com.budbee.proto.HelloRequest;

@ExposedService
public interface Hello {

    @Subscriber(topic = "hello")
    HelloResponse hello(HelloRequest request);
    
    HelloResponse onHello(HelloEvent event);
}
