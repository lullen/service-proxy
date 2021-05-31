package accessone.interfaces;

import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;

import accessone.interfaces.proto.HelloOneRequest;
import accessone.interfaces.proto.HelloOneResponse;

@ExposedService
public interface Hello {
    Response<HelloOneResponse> hello(HelloOneRequest request);
    Response<OneHelloResponse> v2Call(OneHello request);
}
