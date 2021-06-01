package accessone.app;

import java.util.Collections;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import serviceproxy.ServiceProxyConfiguration;


@SpringBootApplication
@Import(ServiceProxyConfiguration.class)
public class Application {

    public static void main(String[] args) {
        var app = new SpringApplication(Application.class);
        app.setDefaultProperties(Map.of(
                "server.port", "5001"
                // "server.http2.enabled", "false",
                // "server.ssl.key-store-type", "PKCS12",
                // "server.ssl.key-store", "classpath:localhost.p12",
                // "server.ssl.key-store-password", "password",
                // "server.ssl.key-alias", "localhost"
                ));
        // app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    // @Bean
    // public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    // return args -> {
    // var server = ctx.getBean(DaprServer.class);
    // server
    // .registerServices(ctx)
    // .start(5001)
    // .awaitTermination();

    // };
    // }
}
