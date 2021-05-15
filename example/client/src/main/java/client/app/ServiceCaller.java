package client.app;

import com.test.proto.HelloRequest;
import com.test.proto.HelloResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import accessone.app.interfaces.HelloOne;
import serviceproxy.model.Response;
import serviceproxy.pubsub.EventPublisher;
import serviceproxy.secret.SecretStore;
import serviceproxy.proxy.ServiceProxy;
import server.interfaces.HelloServer;

@Component
public class ServiceCaller {
    private static final Logger _logger = LogManager.getLogger(ServiceCaller.class);
    private ServiceProxy serviceProxy;
    private EventPublisher eventPublisher;
    private SecretStore secretStore;

    public ServiceCaller(ServiceProxy serviceProxy, EventPublisher eventPublisher, SecretStore secretStore) {
        this.serviceProxy = serviceProxy;
        this.eventPublisher = eventPublisher;
        this.secretStore = secretStore;
    }

    public long call() throws Exception {
        _logger.info("Howdy from call");
        var count = 0;
        var start = System.currentTimeMillis();
        var sp = serviceProxy.create(HelloServer.class);
        var sp2 = serviceProxy.create(HelloOne.class);

        while (count < 100) {
            _logger.info("-------------------------------");
            var request = HelloRequest.newBuilder().setText("!Hello there from call #" + count++ + "!")
                    .setNewText("What's up?").setOtherText("Alright").build();
            _logger.info(request.getText() + " " + request.getNewText());

            var resp = sp
                .hello(request)
                .then(res -> sp.hello(request))
                .onError(error -> {
                    _logger.error("!!!ERROR!!! - " + error.getError());
                    return new Response<HelloResponse>(HelloResponse.newBuilder().setText("Hello").build());
                });
            _logger.info("Response: " + resp.error.getError());

            var resp2 = sp2.hello(request);
            _logger.info("Response2: " + resp2.error.getError());

        }
        _logger.info("Total: {} ms", (System.currentTimeMillis() - start));
        return System.currentTimeMillis() - start;
    }

    public long publish() throws Exception {
        _logger.info("Howdy from proxy");
        var count = 0;
        var start = System.currentTimeMillis();

        while (count < 100) {
            var request = HelloRequest.newBuilder().setText("Hello there from publish #" + count++ + "!")
                    .setNewText("What's up?").setOtherText("Alright").build();

            _logger.info(request.getText() + " " + request.getNewText());
            eventPublisher.publish(Constants.DefaultPubSub, "hello", request);

        }
        _logger.info("Total: " + (System.currentTimeMillis() - start) + " ms");
        return System.currentTimeMillis() - start;
    }

    public long getSecrets() throws Exception {
        _logger.info("Howdy from proxy");
        var count = 0;
        var start = System.currentTimeMillis();

        while (count < 1) {
            var secretNumber = count++ % 2 + 1;
            var secret = secretStore.get(Constants.DefaultSecret, "secret" + secretNumber);
            _logger.info("Got secret value: " + secret);
        }
        _logger.info("Total: {} ms", (System.currentTimeMillis() - start));
        return System.currentTimeMillis() - start;
    }
}
