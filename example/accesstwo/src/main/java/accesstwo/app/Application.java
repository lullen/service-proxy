package accesstwo.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import accesstwo.app.interfaces.HelloTwo;
import serviceproxy.ServiceProxyConfiguration;
import serviceproxy.server.DaprServer;


@SpringBootApplication
@Import(ServiceProxyConfiguration.class)
public class Application {


    public static void main(String[] args) {
        var app = new SpringApplication(Application.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            // System.out.println("Let's inspect the beans provided by Spring Boot:");

            // String[] beanNames = ctx.getBeanDefinitionNames();
            // Arrays.sort(beanNames);
            // for (String beanName : beanNames) {
            //     System.out.println(beanName);
            // }
            var server = ctx.getBean(DaprServer.class);
            server
                .registerServices(ctx)
                .start(5002)
                .awaitTermination();

            // Add client Shutdown Logic
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));

        };
    }

    // public static void main(String[] args) throws IOException, InterruptedException {
    //     Configurator.setRootLevel(Level.INFO);

    //     var injector = Guice.createInjector(new AccessTwoModule(), new ServerModule());

    //     final var service = injector.getInstance(DaprServer.class);
    //     service.start(5002);
    //     service.awaitTermination();
    // }
}
