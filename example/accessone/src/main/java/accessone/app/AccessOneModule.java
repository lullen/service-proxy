package accessone.app;

import java.util.List;

import com.google.inject.AbstractModule;
import accessone.interfaces.HelloOne;
import accessone.service.HelloServiceImpl;
import serviceproxy.helpers.ServiceLoader;

public class AccessOneModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloOne.class).to(HelloServiceImpl.class);

        ServiceLoader.registerServices(List.of(HelloOne.class));
    }

}
