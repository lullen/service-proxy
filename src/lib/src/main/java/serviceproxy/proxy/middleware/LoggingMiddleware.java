package serviceproxy.proxy.middleware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import serviceproxy.model.Response;

@Component
public class LoggingMiddleware extends ProxyMiddleware {

    private static final Logger _logger = LogManager.getLogger(LoggingMiddleware.class);

    @Override
    public <T> void before(String appId, String method, Object request, Class<T> responseClass) {
        _logger.info("Calling '{}' on app '{}'", method, appId);
    }

    @Override
    public <T> void after(String appId, String method, Object request, Response<T> response) {
        if (response.hasError()) {
            _logger.warn("Method '{}' returned '{}' with message '{}'", method, response.error.getStatusCode(),
                    response.error.getErrorMessage());
        } else {
            _logger.info("Method '{}' returned '{}'", method, response.error.getStatusCode());
        }

    }
}
