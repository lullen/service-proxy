package serviceproxy.proxy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import serviceproxy.model.Error;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;
import serviceproxy.proxy.middleware.ProxyMiddleware;

@Component
public class HttpProxy extends BaseServiceProxy implements ServiceProxyProvider {
    private HttpClient httpClient;

    public HttpProxy(Set<ProxyMiddleware> middlewares, HttpClient httpClient) {
        super(middlewares);
        this.httpClient = httpClient;
    }

    @Override()
    public <T> Response<T> invoke(String appId, String method, Object request,
            Class<T> responseClass)
            throws Exception {

        Response<T> methodResult;
        this.runBefore(appId, method, request, responseClass);

        try {
            var ctrlMethod = method.replace('.', '/').toLowerCase();
            var protocol = "http";
            var uri = URI.create(String.format("%s://%s/%s", protocol, appId, ctrlMethod));
            var httpResp = postJSON(uri, request);

            var statusCode = httpResp.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                var resp = parseHttpResponse(httpResp, responseClass);
                methodResult = new Response<T>(resp);
            } else {
                var errorCode = StatusCode.Exception;
                switch (statusCode) {
                    case 409:
                        errorCode = StatusCode.AlreadyExists;
                        break;
                    case 400:
                        errorCode = StatusCode.InvalidInput;
                        break;
                    case 404:
                        errorCode = StatusCode.NotFound;
                        break;
                    case 403:
                        errorCode = StatusCode.Unauthorized;
                        break;
                    default:
                        errorCode = StatusCode.Exception;
                        break;
                }

                methodResult = new Response<T>();
                methodResult.error = new Error(errorCode, httpResp.body());
            }
        } catch (Exception exception) {
            methodResult = new Response<T>();
            methodResult.error = new Error(StatusCode.Exception, exception.getMessage());
        }

        this.runAfter(appId, method, request, methodResult);
        return methodResult;
    }

    private <T> T parseHttpResponse(HttpResponse<String> response, Class<T> clazz)
            throws JsonMappingException, JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var res = objectMapper.readValue(response.body(), clazz);
        return res;
    }

    private HttpResponse<String> postJSON(URI uri, Object request)
            throws IOException, InterruptedException {
        var objectMapper = new ObjectMapper();
        var requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

        var httpRequest = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.send(httpRequest, BodyHandlers.ofString());
    }
}
