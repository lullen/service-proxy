package engine.interfaces;

import engine.interfaces.proto.HelloRequest;
import engine.interfaces.proto.HelloResponse;

import serviceproxy.model.Response;
import serviceproxy.pubsub.Subscriber;
import serviceproxy.server.ExposedService;


@ExposedService
public interface Hello {

    @Subscriber(name = "pubsub", topic = "hello")
    Response<HelloResponse> hello(HelloRequest request);

    Response<EngineHelloResponse> v2Call(EngineHello request);
}
