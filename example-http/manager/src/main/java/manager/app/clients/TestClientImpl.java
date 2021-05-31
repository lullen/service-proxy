package manager.app.clients;

import org.springframework.stereotype.Component;

import engine.interfaces.proto.HelloRequest;
import engine.interfaces.proto.HelloResponse;
import serviceproxy.model.Response;

@Component
public class TestClientImpl implements TestClient {

    @Override
    public Response<HelloResponse> legacyCall(HelloRequest request) {
        var res = HelloResponse.newBuilder().setText("Legacy calls works!").build();
        return new Response<HelloResponse>(res);
    }

    @Override
    public Response<HelloV2Response> v2Call(HelloV2 request) throws Exception {
        var resp = new HelloV2Response();
        resp.text = request.text;
        return new Response<HelloV2Response>(resp);
        ///throw new Exception("sadgsdg asg dsg");
    }

}
