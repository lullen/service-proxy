package serviceproxy.proxy.middleware;

import com.google.protobuf.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import serviceproxy.model.Response;

public class TimingMiddleware extends ProxyMiddleware {

    private static final Logger _logger = LogManager.getLogger(TimingMiddleware.class);

    private long _start = 0;

    @Override
    public <T> void before(String appId, String method, Message request, Class<T> responseClass) {
        _start = System.currentTimeMillis();
    }

    @Override
    public <T> void after(String appId, String method, Message request, Response<T> response) {
        _logger.info("Calling '{}.{}' took {} ms", appId, method, (System.currentTimeMillis() - _start));
    }
}
