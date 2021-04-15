package accessone.interfaces;

import com.test.proto.HelloResponse;

import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;

import com.test.proto.HelloRequest;

@ExposedService
public interface Hello {
    Response<HelloResponse> hello(HelloRequest request);
}
