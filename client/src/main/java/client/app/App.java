package client.app;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import common.proxy.EventPublisher;
import common.proxy.ProxyModule;
import common.proxy.ProxyType;
import common.proxy.ServiceProxy;
import common.proxy.SecretStore;

@SpringBootApplication
public class App {
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.INFO);
        // var injector = Guice.createInjector(new ProxyModule());
        // ServiceProxy.init(ProxyType.Dapr, injector);
        // EventPublisher.init("pubsub");
        // SecretStore.init("secretstore");

        var caller = new ServiceCaller();
        var call = caller.call();
        // var pub = caller.publish();

        System.out.println("Total for call: " + call + " ms");
        // System.out.println("Total for pub: " + pub + " ms");
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            ServiceProxy.init(ProxyType.Dapr, null, ctx);

            EventPublisher.init("pubsub");
            SecretStore.init("secretstore");

            // final var service = ctx.getBean(DaprServer.class);
            // service.start(5000);
            // service.registerServices(List.of(Hello.class));
            // service.registerSubscribers("pubsub");
            // service.awaitTermination();

            var caller = new ServiceCaller();
            var call = caller.call();
            // var pub = caller.publish();

            System.out.println("Total for call: " + call + " ms");
            // System.out.println("Total for pub: " + pub + " ms");

            // System.out.println("Let's inspect the beans provided by Spring Boot:");

            // String[] beanNames = ctx.getBeanDefinitionNames();
            // Arrays.sort(beanNames);
            // for (String beanName : beanNames) {
            // System.out.println(beanName);
            // }

        };
    }
}
