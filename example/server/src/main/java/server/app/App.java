package server.app;

import java.io.IOException;
import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import serviceproxy.proxy.ProxyModule;
import serviceproxy.proxy.ProxyType;
import serviceproxy.server.ServerModule;
import serviceproxy.proxy.ServiceProxy;
import serviceproxy.server.DaprServer;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);

        var injector = Guice.createInjector(new DaprModule(), new ProxyModule(), new ServerModule());
        ServiceProxy.init(ProxyType.Dapr, injector);

        final var service = injector.getInstance(DaprServer.class);
        service.start(5000);
        service.awaitTermination();
    }
}
