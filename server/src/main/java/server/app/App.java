package server.app;

import java.io.IOException;
import java.util.List;

import com.google.inject.Guice;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import common.proxy.ProxyModule;
import common.proxy.ProxyType;
import common.proxy.ServiceProxy;
import common.server.DaprServer;
import server.interfaces.Hello;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);
        SpringApplication.run(App.class, args);
        // var injector = Guice.createInjector(new DaprModule(), new ProxyModule());
        // ServiceProxy.init(ProxyType.Dapr, injector, null);

        // final var service = injector.getInstance(DaprServer.class);
        // service.start(5000);
        // service.registerServices(List.of(Hello.class));
        // service.registerSubscribers("pubsub");
        // service.awaitTermination();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            ServiceProxy.init(ProxyType.Dapr, null, ctx);

            final var service = ctx.getBean(DaprServer.class);
            service.start(5000);
            service.registerServices(List.of(Hello.class));
            service.registerSubscribers("pubsub");
            service.awaitTermination();

            // System.out.println("Let's inspect the beans provided by Spring Boot:");

            // String[] beanNames = ctx.getBeanDefinitionNames();
            // Arrays.sort(beanNames);
            // for (String beanName : beanNames) {
            //     System.out.println(beanName);
            // }

        };
    }
}
