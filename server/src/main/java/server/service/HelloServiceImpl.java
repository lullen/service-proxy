package server.service;

import java.util.Date;

import com.test.proto.HelloRequest;
import com.test.proto.HelloResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.model.Error;
import common.model.Response;
import common.model.StatusCode;
import server.interfaces.Hello;

public class HelloServiceImpl implements Hello {
    private static final Logger _logger = LogManager.getLogger(HelloServiceImpl.class);

    @Override
    public Response<HelloResponse> hello(HelloRequest request) {
        _logger.info("HelloServiceImpl.hello called");
        var response = HelloResponse.newBuilder().setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText()).build();
        _logger.info("Hello {} {}", request.getText(), new Date().getTime());
        if (request.getText().contains("5")) {
            var res = new Response<HelloResponse>(new Error(StatusCode.InvalidInput, "Invalid stuff here"));
            res.result = response;
            return res;
        }
        return new Response<HelloResponse>(response);
    }

}
