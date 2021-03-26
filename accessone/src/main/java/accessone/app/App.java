package accessone.app;

import java.io.IOException;
import java.util.List;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import common.server.DaprServer;
import accessone.interfaces.Hello;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);

        var injector = Guice.createInjector(new DaprModule());

        final var service = injector.getInstance(DaprServer.class);
        service.start(5001);
        service.registerServices(List.of(Hello.class));
        service.registerSubscribers("pubsub");
        service.awaitTermination();
    }
}
