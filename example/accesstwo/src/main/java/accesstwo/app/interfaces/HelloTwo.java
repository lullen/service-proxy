package accesstwo.app.interfaces;

import com.test.proto.HelloResponse;

import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;

import com.test.proto.HelloRequest;

@ExposedService
public interface HelloTwo {
    Response<HelloResponse> hello(HelloRequest request);
}
