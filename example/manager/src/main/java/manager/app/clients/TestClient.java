package manager.app.clients;

import engine.interfaces.proto.HelloRequest;
import engine.interfaces.proto.HelloResponse;
import serviceproxy.model.Response;
import serviceproxy.server.ExposedService;

@ExposedService(legacy = true)
public interface TestClient {
    Response<HelloResponse> legacyCall(HelloRequest request);
}
