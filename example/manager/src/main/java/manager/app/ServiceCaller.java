package manager.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import accessone.interfaces.proto.HelloOneRequest;
import manager.app.clients.TestClient;
import serviceproxy.model.Response;
import serviceproxy.pubsub.EventPublisher;
import serviceproxy.secret.SecretStore;
import serviceproxy.proxy.ServiceProxy;
import engine.interfaces.proto.HelloRequest;
import engine.interfaces.proto.HelloResponse;

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
        var sp = serviceProxy.create(engine.interfaces.Hello.class);
        var sp2 = serviceProxy.create(accessone.interfaces.Hello.class);
        var sp3 = serviceProxy.create(TestClient.class);

        while (count < 2) {
            _logger.info("-------------------------------");
            var request = HelloRequest.newBuilder().setText("!Hello there from call #" + count++ + "!")
                    .setNewText("What's up?").setOtherText("Alright").build();
            _logger.info(request.getText() + " " + request.getNewText());

            var resp = sp.hello(request).then(res -> sp.hello(request)).onError(error -> {
                _logger.error("!!!ERROR!!! - " + error.getError());
                return new Response<HelloResponse>(HelloResponse.newBuilder().setText("Hello").build());
            });
            _logger.info("Response: " + resp.error.getError());

            var request2 = HelloOneRequest.newBuilder().setText(request.getText()).setNewText(request.getNewText())
                    .setOtherText(request.getOtherText()).build();
            var resp2 = sp2.hello(request2);
            _logger.info("Response2: " + resp2.error.getError());
            
            
            var resp3 = sp3.legacyCall(request);
            _logger.info("Legacy call: " + resp3.result.getText());
            

        }
        _logger.info("Total: {} ms", (System.currentTimeMillis() - start));
        return System.currentTimeMillis() - start;
    }

    public long publish() throws Exception {
        _logger.info("Howdy from proxy");
        var count = 0;
        var start = System.currentTimeMillis();

        while (count < 2) {
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
