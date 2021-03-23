package server.interfaces;

import com.budbee.proto.HelloResponse;

import common.model.Response;
import common.pubsub.Subscriber;
import common.server.ExposedService;

import com.budbee.proto.HelloRequest;

@ExposedService
public interface Hello {

    @Subscriber(topic = "hello")
    Response<HelloResponse> hello(HelloRequest request);
}
