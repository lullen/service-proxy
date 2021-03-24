package server.interfaces;

import com.test.proto.HelloResponse;

import common.model.Response;
import common.pubsub.Subscriber;
import common.server.ExposedService;

import com.test.proto.HelloRequest;

@ExposedService
public interface Hello {

    @Subscriber(topic = "hello")
    Response<HelloResponse> hello(HelloRequest request);
}
