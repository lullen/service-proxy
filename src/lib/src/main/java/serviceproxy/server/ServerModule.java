package serviceproxy.proxy;

import com.google.inject.AbstractModule;
import serviceproxy.server.DaprServer;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DaprServer.class);
    }
}
