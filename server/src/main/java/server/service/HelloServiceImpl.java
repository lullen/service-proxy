package server.service;

import java.util.Date;

import com.test.proto.HelloRequest;
import com.test.proto.HelloResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import common.model.Error;
import common.model.Response;
import common.model.StatusCode;
import common.proxy.ServiceProxy;
import server.interfaces.Hello;

@Service
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

        var sp = ServiceProxy.create(accesstwo.interfaces.Hello.class);
        var res2 = sp.hello(request);

        if (res2.result != null) {
            System.out.println(res2.result.getText());
        }

        return new Response<HelloResponse>(response);
    }

}
