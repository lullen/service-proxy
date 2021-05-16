package server.service;

import java.util.Date;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import accesstwo.interfaces.proto.HelloTwoRequest;
import server.interfaces.HelloServer;
import server.interfaces.proto.HelloRequest;
import server.interfaces.proto.HelloResponse;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;
import serviceproxy.proxy.ServiceProxy;

@Component
public class HelloServerServiceImpl implements HelloServer {

    private ServiceProxy serviceProxy;

    public HelloServerServiceImpl(ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    private static final Logger _logger = LogManager.getLogger(HelloServerServiceImpl.class);

    @Override
    public Response<HelloResponse> hello(HelloRequest request) {
        _logger.info("HelloServiceImpl.hello called");
        var response = HelloResponse.newBuilder().setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText()).build();
        _logger.info("Hello {} {}", request.getText(), new Date().getTime());
        if (request.getText().contains("2")) {
            _logger.warn("Returning error as \"{}\" contains \"2\"", request.getText());
            var res = new Response<HelloResponse>(new Error(StatusCode.InvalidInput, "Invalid stuff here"));
            res.result = response;
            return res;
        }

        var res = new Response<HelloResponse>(response);
        var result = res.then(r -> {
            _logger.info("running method 1");
            return test();
        }).then(r -> {
            _logger.info("running method 2");
            return test2("Hello there #" + r.result);
        }).onError(error -> {
            _logger.error("Error returned: " + error.getError());
            return new Response<TestClass2>(error);
        });
        _logger.info(result.hasError().toString());

        var sp = serviceProxy.create(accesstwo.interfaces.HelloTwo.class);
        
        var request2 = HelloTwoRequest.newBuilder().setText(request.getText()).setNewText(request.getNewText())
        .setOtherText(request.getOtherText()).build();
        var res2 = sp.hello(request2);
        if (res2.result != null) {
            System.out.println(res2.result.getText());
        }

        return new Response<HelloResponse>(response);
    }

    private Response<TestClass1> test() {
        var t = new TestClass1();
        t.number = 5;
        // return new Response<TestClass1>(t);
        return new Response<TestClass1>(new Error(StatusCode.AlreadyExists, "Already exists"));
    }

    private Response<TestClass2> test2(String hello) {
        var t = new TestClass2();
        t.hello = hello;
        return new Response<TestClass2>(t);
    }
}
