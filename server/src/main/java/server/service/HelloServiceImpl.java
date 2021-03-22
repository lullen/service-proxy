package server.service;

import java.util.Date;

import com.budbee.proto.HelloRequest;
import com.budbee.proto.HelloResponse;

import server.interfaces.Hello;

public class HelloServiceImpl implements Hello {

    @Override
    public HelloResponse hello(HelloRequest request) {
        var response = HelloResponse.newBuilder().setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText()).build();
        System.out.println("Hello " + request.getText() + " " + new Date().getTime());
        System.out.println(request.getText());
        return response;
    }

}
