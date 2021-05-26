package accessone.app.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import accessone.interfaces.Hello;
import accessone.interfaces.proto.HelloOneRequest;
import accessone.interfaces.proto.HelloOneResponse;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;

@Component
public class HelloOneServiceImpl implements Hello {
    private static final Logger _logger = LogManager.getLogger(HelloOneServiceImpl.class);

    @Override
    public Response<HelloOneResponse> hello(HelloOneRequest request) {
        _logger.info("!!!HelloOneServiceImpl.hello called!!!");
        var response = HelloOneResponse
            .newBuilder()
            .setText("Hello " + request.getText() + " " + request.getOtherText())
            .setOtherText(request.getOtherText())
            .build();

        _logger.info("Hello {} {}", request.getText(), new Date().getTime());

        if (request.getText().contains("2")) {
            _logger.warn("Returning error as \"{}\" contains \"2\"", request.getText());
            var res = new Response<HelloOneResponse>(new serviceproxy.model.Error(StatusCode.InvalidInput, "Invalid stuff here"));
            res.result = response;
            return res;
        }
        return new Response<HelloOneResponse>(response);
    }

}
