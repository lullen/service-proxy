package accessone.app;

import java.util.Collections;
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
        app.setDefaultProperties(Collections.singletonMap("server.port", "5001"));
        // app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    // @Bean
    // public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    //     return args -> {
    //         var server = ctx.getBean(DaprServer.class);
    //         server
    //             .registerServices(ctx)
    //             .start(5001)
    //             .awaitTermination();

    //     };
    // }
}
