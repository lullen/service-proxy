package accessone.app;

import com.google.inject.AbstractModule;
import accessone.interfaces.HelloOne;
import accessone.service.HelloServiceImpl;

public class AccessOneModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloOne.class).to(HelloServiceImpl.class);
    }

}
