package server.app;

import com.google.inject.AbstractModule;
import serviceproxy.server.DaprServer;
import server.interfaces.HelloServer;
import server.service.HelloServiceImpl;

public class DaprModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloServer.class).to(HelloServiceImpl.class);
    }

}
