package client.app;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import common.proxy.ProxySettings;
import common.proxy.ProxyType;
import common.proxy.ServiceProxy;

public class App {
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.DEBUG);
        var settings = new ProxySettings();
        settings.type = ProxyType.Dapr;
        settings.pubsubName = "pubsub";
        settings.secretStoreName = "secretstore";
        ServiceProxy.init(settings);

        var caller = new ServiceCaller();
        var call = caller.call();
        var pub = caller.publish();
        
        System.out.println("Total for call: " + call + " ms");
        System.out.println("Total for pub: " + pub + " ms");
    }
}
