package serviceproxy.proxy;

import java.util.Set;

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
public class DaprProxy extends BaseServiceProxy implements IServiceProxy {

    public DaprProxy(Set<ProxyMiddleware> middlewares) {
        super(middlewares);
    }

    @Override()
    public <T> Response<T> invoke(String appId, String method, Object request, Class<T> responseClass)
            throws Exception {

        Response<T> methodResult;
        this.runBefore(appId, method, request, responseClass);

        try (DaprClient client = new DaprClientBuilder().build()) {
            try {
                var resp = client.invokeMethod(appId, method, request, HttpExtension.NONE, responseClass).block();
                methodResult = new Response<T>(resp);
            } catch (DaprException exception) {
                var errorCode = StatusCode.Exception;
                Status.Code code;
                try {
                    code = Status.Code.valueOf(exception.getErrorCode());
                } catch (IllegalArgumentException ex) {
                    code = Status.Code.INTERNAL;
                }

                switch (code) {
                    case ALREADY_EXISTS:
                        errorCode = StatusCode.AlreadyExists;
                        break;
                    case INVALID_ARGUMENT:
                        errorCode = StatusCode.InvalidInput;
                        break;
                    case NOT_FOUND:
                        errorCode = StatusCode.NotFound;
                        break;
                    case UNAUTHENTICATED:
                        errorCode = StatusCode.Unauthorized;
                        break;
                    default:
                        errorCode = StatusCode.Exception;
                        break;
                }

                methodResult = new Response<T>();
                methodResult.error = new Error(errorCode, exception.getMessage());
            }
        }

        this.runAfter(appId, method, request, methodResult);
        return methodResult;
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