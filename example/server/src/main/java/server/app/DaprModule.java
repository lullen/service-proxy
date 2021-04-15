package server.app;

import com.google.inject.AbstractModule;
import serviceproxy.server.DaprServer;
import server.interfaces.Hello;
import server.service.HelloServiceImpl;

public class DaprModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Hello.class).to(HelloServiceImpl.class);
        bind(DaprServer.class);
    }

}
