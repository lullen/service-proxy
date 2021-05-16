package client.app.clients;

import server.interfaces.proto.HelloRequest;
import server.interfaces.proto.HelloResponse;
import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;

@ExposedService(legacy = true)
public interface TestClient {
    Response<HelloResponse> legacyCall(HelloRequest request);
}
