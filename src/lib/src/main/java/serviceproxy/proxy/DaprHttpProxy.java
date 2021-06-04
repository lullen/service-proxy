package serviceproxy.proxy;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import com.google.protobuf.Message;

import org.springframework.stereotype.Component;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import io.dapr.exceptions.DaprException;
import io.dapr.serializer.DefaultObjectSerializer;
import io.grpc.Status;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;
import serviceproxy.proxy.middleware.ProxyMiddleware;

@Component
public class DaprHttpProxy extends BaseServiceProxy
        implements ServiceProxyProvider, EventPublisherProvider, SecretStoreProvider {
    private HttpServletRequest request;

    public DaprHttpProxy(Set<ProxyMiddleware> middlewares, HttpServletRequest request) {
        super(middlewares);
        this.request = request;
    }

    @Override()
    public <T> Response<T> invoke(String appId, String method, Object request,
            Class<T> responseClass)
            throws Exception {

        Response<T> methodResult;
        this.runBefore(appId, method, request, responseClass);

        try (DaprClient client = new DaprClientBuilder().build()) {
            try {
                System.out.println("exception.getErrorCode()");
                var controllerMethod = method.replace('.', '/').toLowerCase();

                var call = client
                        .invokeMethod(appId, controllerMethod, request, HttpExtension.POST,
                                responseClass);
                var traceparent = this.request.getHeader("traceparent");
                if (traceparent != null && !traceparent.isEmpty()) {
                    call = call.subscriberContext(c -> c.put("traceparent", traceparent));
                }

                var resp = call.block();
                methodResult = new Response<T>(resp);
            } catch (DaprException exception) {
                tryParseStatusCode(exception.getMessage());
                methodResult = new Response<T>();
                methodResult.error = new Error(errorCode, exception.getMessage());
            }
        }

        this.runAfter(appId, method, request, methodResult);
        return methodResult;
    }

    private int tryParseStatusCode(String message) {
        if (message.contains("HTTP status code: 403")) {
            return 403;
        } else if (message.contains("HTTP status code: 409")) {
            return 409;
        } else if (message.contains("HTTP status code: 404")) {
            return 404;
        } else if (message.contains("HTTP status code: 400")) {
            return 400;
        } else {
            return 500;
        }
    }

    @Override
    public void publish(String pubsubName, String topic, Message request) throws Exception {
        var json = com.google.protobuf.util.JsonFormat.printer().print(request);
        var serializedRequest = new DefaultObjectSerializer().serialize(json);

        try (DaprClient client = new DaprClientBuilder().build()) {
            client.publishEvent(pubsubName, topic, serializedRequest).block();
        }
    }

    @Override
    public String getSecret(String secretStoreName, String key) throws Exception {
        try (var client = new DaprClientBuilder().build()) {
            return client.getSecret(secretStoreName, key).block().get(key);
        }
    }
}
