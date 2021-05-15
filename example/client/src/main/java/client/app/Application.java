package client.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import serviceproxy.ServiceProxyConfiguration;
import serviceproxy.proxy.ProxyType;
import serviceproxy.proxy.ServiceProxy;
import serviceproxy.pubsub.EventPublisher;

@SpringBootApplication
@Import(ServiceProxyConfiguration.class)
public class Application {

    public static void main(String[] args) {
        var app = new SpringApplication(Application.class);
        // app.setWebApplicationType(WebApplicationType.);
        app.run(args);
        // SpringApplication.run(App.class, args);
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
            
            ServiceProxy.init(ProxyType.Dapr);
            EventPublisher.init(ProxyType.Dapr);

            // var caller = ctx.getBean(ServiceCaller.class);

            // var call = caller.call();
            // var pub = caller.publish();

            // TODO: configure secret store
            // var sec = caller.getSecrets();

            // System.out.println("Total for call: " + call + " ms");
            // System.out.println("Total for pub: " + pub + " ms");
            // System.out.println("Total for secrets: " + sec + " ms");

        };
    }

    // public static void main(String[] args) throws Exception {
    // Configurator.setRootLevel(Level.INFO);
    // var injector = Guice.createInjector(new ProxyModule(), new ServerModule());
    // ServiceProxy.init(ProxyType.Dapr, injector);

    // var caller = new ServiceCaller();
    // var call = caller.call();
    // var pub = caller.publish();

    // // TODO: configure secret store
    // // var sec = caller.getSecrets();

    // System.out.println("Total for call: " + call + " ms");
    // System.out.println("Total for pub: " + pub + " ms");
    // // System.out.println("Total for secrets: " + sec + " ms");
    // }
}
