package accessone.app.service;

import java.util.Date;

import com.test.proto.HelloRequest;
import com.test.proto.HelloResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import accessone.app.interfaces.HelloOne;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;

@Component
public class HelloOneServiceImpl implements HelloOne {
    private static final Logger _logger = LogManager.getLogger(HelloOneServiceImpl.class);

    @Override
    public Response<HelloResponse> hello(HelloRequest request) {
        _logger.info("HelloServiceImpl.hello called");
        var response = HelloResponse.newBuilder().setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText()).build();
        _logger.info("Hello {} {}", request.getText(), new Date().getTime());
        if (request.getText().contains("2")) {
            _logger.warn("Returning error as \"{}\" contains \"2\"", request.getText());
            var res = new Response<HelloResponse>(new serviceproxy.model.Error(StatusCode.InvalidInput, "Invalid stuff here"));
            res.result = response;
            return res;
        }
        return new Response<HelloResponse>(response);
    }

}
