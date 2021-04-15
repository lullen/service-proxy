package accessone.app;

import com.google.inject.AbstractModule;
import accessone.interfaces.Hello;
import accessone.service.HelloServiceImpl;
import serviceproxy.server.DaprServer;

public class DaprModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Hello.class).to(HelloServiceImpl.class);
        bind(DaprServer.class);
    }

}
