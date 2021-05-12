package accesstwo.app;

import com.google.inject.AbstractModule;
import serviceproxy.server.DaprServer;
import accesstwo.interfaces.HelloTwo;
import accesstwo.service.HelloServiceImpl;

public class AccessTwoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloTwo.class).to(HelloServiceImpl.class);
        bind(DaprServer.class);
    }

}
