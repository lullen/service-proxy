package accesstwo.app;

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

import common.server.DaprServer;
import accesstwo.interfaces.Hello;
import common.proxy.ServiceProxy;
import common.proxy.ProxyType;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);
        SpringApplication.run(App.class, args);

        // var injector = Guice.createInjector(new DaprModule());

        // final var service = injector.getInstance(DaprServer.class);
        // service.start(5002);
        // service.registerServices(List.of(Hello.class));
        // service.awaitTermination();
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            ServiceProxy.init(ProxyType.Dapr, null, ctx);

            final var service = ctx.getBean(DaprServer.class);
            service.start(5002);
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
