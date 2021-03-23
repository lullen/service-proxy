package client.app;

import com.budbee.proto.HelloRequest;
import com.budbee.proto.HelloResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.proxy.ServiceProxy;

public class ServiceCaller {
    private static final Logger _logger = LogManager.getLogger(ServiceCaller.class);

    public long call() throws Exception {
        _logger.info("Howdy from call");
        var count = 0;
        var start = System.currentTimeMillis();
        var sp = ServiceProxy.create();
        while (count < 10) {
            var request = HelloRequest.newBuilder().setText("Hello there from call #" + count++ + "!")
                    .setNewText("What's up?").setOtherText("Alright").build();
            _logger.info(request.getText() + " " + request.getNewText());

            var resp = sp.invoke("server", "Hello.hello", request, HelloResponse.class);
            _logger.info("Response: " + resp.error.getError());
        }
        _logger.info("Total: {} ms", (System.currentTimeMillis() - start));
        return System.currentTimeMillis() - start;
    }

    public long publish() throws Exception {
        _logger.info("Howdy from proxy");
        var count = 0;
        var start = System.currentTimeMillis();
        var client = ServiceProxy.create();

        while (count < 1) {
            var request = HelloRequest.newBuilder().setText("Hello there from publish #" + count++ + "!")
                    .setNewText("What's up?").setOtherText("Alright").build();

            _logger.info(request.getText() + " " + request.getNewText());
            client.publish("hello", request);

        }
        _logger.info("Total: " + (System.currentTimeMillis() - start) + " ms");
        return System.currentTimeMillis() - start;
    }

    public long getSecrets() throws Exception {
        _logger.info("Howdy from proxy");
        var count = 0;
        var start = System.currentTimeMillis();
        var client = ServiceProxy.create();

        while (count < 10) {
            var secretNumber = count++ % 2 + 1;
            var secret = client.secret("secret" + secretNumber);
            _logger.info("Got secret value: " + secret);
        }
        _logger.info("Total: {} ms", (System.currentTimeMillis() - start));
        return System.currentTimeMillis() - start;
    }
}
