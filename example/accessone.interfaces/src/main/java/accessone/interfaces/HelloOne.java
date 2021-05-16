package accessone.interfaces;

import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;

import accessone.interfaces.proto.HelloOneRequest;
import accessone.interfaces.proto.HelloOneResponse;

@ExposedService
public interface HelloOne {
    Response<HelloOneResponse> hello(HelloOneRequest request);
}
