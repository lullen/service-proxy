package accessone.app;

import java.io.IOException;
import java.util.List;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import accessone.interfaces.HelloOne;
import serviceproxy.server.ServerModule;
import serviceproxy.server.DaprServer;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);

        var injector = Guice.createInjector(new AccessOneModule(), new ServerModule());

        final var service = injector.getInstance(DaprServer.class);
        service.registerServices(List.of(HelloOne.class));
        service.start(5001);
        service.awaitTermination();
    }
}
