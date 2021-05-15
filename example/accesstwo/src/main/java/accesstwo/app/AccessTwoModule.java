package accesstwo.app;

import java.util.List;

import com.google.inject.AbstractModule;
import accesstwo.interfaces.HelloTwo;
import accesstwo.service.HelloServiceImpl;
import serviceproxy.helpers.ServiceLoader;

public class AccessTwoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloTwo.class).to(HelloServiceImpl.class);

        ServiceLoader.registerServices(List.of(HelloTwo.class));
    }

}
