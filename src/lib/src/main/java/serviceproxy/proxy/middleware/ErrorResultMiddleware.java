package serviceproxy.proxy.middleware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import serviceproxy.model.Response;


@Component
public class ErrorResultMiddleware extends ProxyMiddleware {
    private static final Logger _logger = LogManager.getLogger(ErrorResultMiddleware.class);

    @Override
    public <T> void before(String appId, String method, Object request, Class<T> responseClass) {}

    @Override
    public <T> void after(String appId, String method, Object request, Response<T> response) {
        if (response.hasError() && response.result != null) {
            response.result = null;
            _logger.warn("Removing result as response has errors.");
        }
    }
}
