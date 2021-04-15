package serviceproxy.proxy.middleware;

import com.google.protobuf.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import serviceproxy.model.Response;

public class ErrorResultMiddleware extends ProxyMiddleware {
    private static final Logger _logger = LogManager.getLogger(ErrorResultMiddleware.class);

    @Override
    public <T> void before(String appId, String method, Message request, Class<T> responseClass) {
    }

    @Override
    public <T> void after(String appId, String method, Message request, Response<T> response) {
        if (response.hasError() && response.result != null) {
            response.result = null;
            _logger.warn("Removing result as response has errors.");
        }
    }
}
