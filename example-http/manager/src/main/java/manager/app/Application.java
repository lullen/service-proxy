package manager.app;

import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import serviceproxy.ServiceProxyConfiguration;
import serviceproxy.helpers.ServiceLoader;
import serviceproxy.proxy.ProxyType;
import serviceproxy.proxy.ServiceProxy;
import serviceproxy.pubsub.EventPublisher;
import serviceproxy.secret.SecretStore;

@SpringBootApplication
@Import(ServiceProxyConfiguration.class)
public class Application {

    public static void main(String[] args) {
        var app = new SpringApplication(Application.class);
        app.setDefaultProperties(Map.of(
                "server.port", "8080"
                // "server.http2.enabled", "false",
                // "server.ssl.key-store-type", "PKCS12",
                // "server.ssl.key-store", "classpath:localhost.p12",
                // "server.ssl.key-store-password", "password",
                // "server.ssl.key-alias", "localhost"
                ));
        app.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            ServiceProxy.init(ProxyType.Http);
            // EventPublisher.init(ProxyType.Http);
            SecretStore.init(ProxyType.Http);
            ServiceLoader.registerServices(ctx, ProxyType.Http);
        };
    }
}
