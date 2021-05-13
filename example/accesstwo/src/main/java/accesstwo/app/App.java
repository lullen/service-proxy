package accesstwo.app;

import java.io.IOException;
import java.util.List;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import serviceproxy.server.ServerModule;
import serviceproxy.server.DaprServer;
import accesstwo.interfaces.HelloTwo;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);

        var injector = Guice.createInjector(new AccessTwoModule(), new ServerModule());

        final var service = injector.getInstance(DaprServer.class);
        service.registerServices(List.of(HelloTwo.class));
        service.start(5002);
        service.awaitTermination();
    }
}
