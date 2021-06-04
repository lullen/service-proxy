package engine.service;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import accessone.interfaces.OneHello;
import engine.interfaces.EngineHello;
import engine.interfaces.EngineHelloResponse;
import engine.interfaces.Hello;
import engine.interfaces.proto.HelloRequest;
import engine.interfaces.proto.HelloResponse;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;
import serviceproxy.proxy.ServiceProxy;

@Component
@RestController
public class HelloServerServiceImpl implements Hello {

    private ServiceProxy serviceProxy;
    private HttpServletRequest req;

    public HelloServerServiceImpl(ServiceProxy serviceProxy, HttpServletRequest req) {
        this.serviceProxy = serviceProxy;
        this.req = req;
    }
    private static final Logger _logger = LogManager.getLogger(HelloServerServiceImpl.class);

    @Override
    public Response<HelloResponse> hello(HelloRequest request) {
        _logger.info("HelloServerServiceImpl.hello called");
        var response = HelloResponse
                .newBuilder()
                .setText("Hello " + request.getText() + " " + request.getOtherText())
                .setOtherText(request.getOtherText())
                .build();

        _logger.info("Hello {} {}", request.getText(), new Date().getTime());
        if (request.getText().contains("2")) {
            _logger.warn("Returning error as \"{}\" contains \"2\"", request.getText());
            var res = new Response<HelloResponse>(
                    new Error(StatusCode.InvalidInput, "Invalid stuff here"));
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
            _logger.error("Error returned: " + error.getErrorMessage());
            return new Response<TestClass2>(error);
        });
        _logger.info(result.hasError().toString());

        // return new Response<HelloResponse>(new Error(StatusCode.InvalidInput, "Invalid!"));
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


    @Override
    @PostMapping("/hello/v2call")
    // @PostMapping("/Hello/v2Call")
    public Response<EngineHelloResponse> v2Call(@RequestBody EngineHello request) {
        var names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            var name = names.nextElement();
            System.out.println(name + " - " + req.getHeader(name));
        }
        //  (n -> System.out);

        System.out.println("inside v2Call with text " + request.text);
        request = new EngineHello();
        request.text = "hello there!!!";
        
        
        // Ext call
        // var access = serviceProxy.create(accessone.interfaces.Hello.class);
        // var respOne = access.v2Call(new OneHello("Saying hello to one!"));
        // System.out.println("Called v2Call");
        //

        var resp = new Response<EngineHelloResponse>(new EngineHelloResponse(request.text));
        resp.error = new Error(StatusCode.InvalidInput, "Unauthorized!");
        return resp;
    }

    @GetMapping("/Hello/hi")
    public String hi() {
        return "HI!";
    }
}
