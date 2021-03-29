package common.proxy.middleware;

import com.google.protobuf.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import common.model.Response;

@Service
public class LoggingMiddleware extends ProxyMiddleware {

    private static final Logger _logger = LogManager.getLogger(LoggingMiddleware.class);

    @Override
    public <T> void before(String appId, String method, Message request, Class<T> responseClass) {
        _logger.info("Calling '{}.{}' on app '{}'", appId, method, appId);
    }

    @Override
    public <T> void after(String appId, String method, Message request, Response<T> response) {
        if (response.hasError()) {
            _logger.warn("Method '{}.{}' returned '{}' with message '{}'", appId, method,
                    response.error.getStatusCode(), response.error.getError());
        } else {
            _logger.info("Method '{}.{}' returned '{}'", appId, method, response.error.getStatusCode());
        }

    }
}
