package serviceproxy.server;

import java.io.IOException;

import org.springframework.context.ApplicationContext;

public interface ServerHost {
    ServerHost start(int port) throws IOException;
    void awaitTermination() throws InterruptedException;
    ServerHost registerServices(ApplicationContext applicationContext);
}
