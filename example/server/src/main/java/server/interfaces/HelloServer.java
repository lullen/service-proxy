package server.interfaces;

import com.test.proto.HelloResponse;

import serviceproxy.model.Response;
import serviceproxy.pubsub.Subscriber;
import serviceproxy.server.ExposedService;

import com.test.proto.HelloRequest;

@ExposedService
public interface HelloServer {

    @Subscriber(name = "pubsub", topic = "hello")
    Response<HelloResponse> hello(HelloRequest request);
}
