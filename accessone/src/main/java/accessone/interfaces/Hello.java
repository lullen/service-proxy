package accessone.interfaces;

import com.test.proto.HelloResponse;

import common.model.Response;
import common.server.ExposedService;

import com.test.proto.HelloRequest;

@ExposedService
public interface Hello {
    Response<HelloResponse> hello(HelloRequest request);
}
