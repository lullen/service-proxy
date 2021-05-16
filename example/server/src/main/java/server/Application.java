package server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import serviceproxy.ServiceProxyConfiguration;
import serviceproxy.proxy.ProxyType;
import serviceproxy.proxy.ServiceProxy;
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
            ServiceProxy.init(ProxyType.Dapr);
            var server = ctx.getBean(DaprServer.class);
            server
                .registerServices(ctx)
                .start(5000)
                .awaitTermination();
            // server.registerServices(List.of(HelloServer.class));

            // // Add client Shutdown Logic
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));

        };
    }
}
