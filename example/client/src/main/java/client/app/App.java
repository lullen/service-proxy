package client.app;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import serviceproxy.proxy.EventPublisher;
import serviceproxy.proxy.ProxyModule;
import serviceproxy.proxy.ProxyType;
import serviceproxy.proxy.ServiceProxy;
import serviceproxy.proxy.SecretStore;

public class App {
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.INFO);
        var injector = Guice.createInjector(new ProxyModule());
        ServiceProxy.init(ProxyType.Dapr, injector);
        EventPublisher.init("pubsub");
        SecretStore.init("secretstore");

        var caller = new ServiceCaller();
        var call = caller.call();
        // var pub = caller.publish();

        System.out.println("Total for call: " + call + " ms");
        // System.out.println("Total for pub: " + pub + " ms");
    }
}
