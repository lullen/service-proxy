package accessone.app;

import com.google.inject.AbstractModule;
import common.server.DaprServer;
import accessone.interfaces.Hello;
import accessone.service.HelloServiceImpl;

public class DaprModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Hello.class).to(HelloServiceImpl.class);
        bind(DaprServer.class);
    }

}
