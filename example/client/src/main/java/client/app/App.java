package client.app;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import serviceproxy.proxy.ProxyModule;
import serviceproxy.proxy.ProxyType;
import serviceproxy.proxy.ServiceProxy;
import serviceproxy.server.ServerModule;

public class App {
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.INFO);
        var injector = Guice.createInjector(new ProxyModule(), new ServerModule());
        ServiceProxy.init(ProxyType.Dapr, injector);

        var caller = new ServiceCaller();
        var call = caller.call();
        var pub = caller.publish();

        // TODO: configure secret store
        // var sec = caller.getSecrets();

        System.out.println("Total for call: " + call + " ms");
        System.out.println("Total for pub: " + pub + " ms");
        // System.out.println("Total for secrets: " + sec + " ms");
    }
}
