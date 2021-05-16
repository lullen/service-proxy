package server.interfaces;

import server.interfaces.proto.HelloRequest;
import server.interfaces.proto.HelloResponse;

import serviceproxy.model.Response;
import serviceproxy.pubsub.Subscriber;
import serviceproxy.server.ExposedService;


@ExposedService
public interface HelloServer {

    @Subscriber(name = "pubsub", topic = "hello")
    Response<HelloResponse> hello(HelloRequest request);
}
