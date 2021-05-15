package server.app;

import java.util.List;

import com.google.inject.AbstractModule;
import server.interfaces.HelloServer;
import server.service.HelloServiceImpl;
import serviceproxy.helpers.ServiceLoader;

public class DaprModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloServer.class).to(HelloServiceImpl.class);

        ServiceLoader.registerServices(List.of(HelloServer.class));
    }

}
