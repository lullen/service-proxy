package manager.app;

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
        app.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            ServiceProxy.init(ProxyType.Dapr);
            EventPublisher.init(ProxyType.Dapr);
            SecretStore.init(ProxyType.Dapr);
            ServiceLoader.registerServices(ctx);
        };
    }
}
