package server.service;

import java.util.Date;

import com.budbee.proto.HelloRequest;
import com.budbee.proto.HelloResponse;

import common.model.Error;
import common.model.Response;
import common.model.StatusCode;
import server.interfaces.Hello;

public class HelloServiceImpl implements Hello {

    @Override
    public Response<HelloResponse> hello(HelloRequest request) {
        System.out.print("HelloServiceImpl.hello called");
        var response = HelloResponse.newBuilder().setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText()).build();
        System.out.println("Hello " + request.getText() + " " + new Date().getTime());
        System.out.println(request.getText());
        if (request.getText().contains("5")) {
            return new Response<HelloResponse>(
                new Error(StatusCode.InvalidInput, "Invalid stuff here")
            );
        }
        return new Response<HelloResponse>(response);
    }

}
