package accesstwo.app.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import accesstwo.interfaces.HelloTwo;
import accesstwo.interfaces.proto.HelloTwoRequest;
import accesstwo.interfaces.proto.HelloTwoResponse;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;

@Component
public class HelloTwoServiceImpl implements HelloTwo {
    private static final Logger _logger = LogManager.getLogger(HelloTwoServiceImpl.class);

    @Override
    public Response<HelloTwoResponse> hello(HelloTwoRequest request) {
        _logger.info("HelloServiceImpl.hello called");
        var response = HelloTwoResponse.newBuilder().setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText()).build();
        _logger.info("Hello {} {}", request.getText(), new Date().getTime());
        if (request.getText().contains("2")) {
            _logger.warn("Returning error as \"{}\" contains \"2\"", request.getText());
            var res = new Response<HelloTwoResponse>(new Error(StatusCode.InvalidInput, "Invalid stuff here"));
            res.result = response;
            return res;
        }



        return new Response<HelloTwoResponse>(response);
    }

}
