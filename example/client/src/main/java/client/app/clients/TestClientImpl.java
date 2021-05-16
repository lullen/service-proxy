package client.app.clients;

import org.springframework.stereotype.Component;

import server.interfaces.proto.HelloRequest;
import server.interfaces.proto.HelloResponse;
import serviceproxy.model.Response;

@Component
public class TestClientImpl implements TestClient {

    @Override
    public Response<HelloResponse> legacyCall(HelloRequest request) {
        var res = HelloResponse.newBuilder().setText("Legacy calls works!").build();
        return new Response<HelloResponse>(res);
    }
    
}
