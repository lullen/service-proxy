package accesstwo.interfaces;

import accesstwo.interfaces.proto.HelloTwoRequest;
import accesstwo.interfaces.proto.HelloTwoResponse;
import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;


@ExposedService
public interface HelloTwo {
    Response<HelloTwoResponse> hello(HelloTwoRequest request);
}
