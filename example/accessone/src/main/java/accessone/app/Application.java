package accessone.app;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

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
            var server = ctx.getBean(DaprServer.class);
            server
                .registerServices(ctx)
                .start(5001)
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

    //     var injector = Guice.createInjector(new AccessOneModule(), new ServerModule());

    //     final var service = injector.getInstance(DaprServer.class);
    //     service.start(5001);
    //     service.awaitTermination();
    // }
}
