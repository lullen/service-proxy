package client.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import client.app.ServiceCaller;
import client.app.models.ServiceResult;

@RestController
public class ServiceController {

    private ServiceCaller serviceCaller;

    public ServiceController(ServiceCaller serviceCaller) {
        this.serviceCaller = serviceCaller;
    }

    @GetMapping("/service")
    ServiceResult run() throws Exception {
        var call = serviceCaller.call();
        var pub = serviceCaller.publish();

        var res = new ServiceResult(
            "Total for call: " + call + " ms",
            "Total for publish: " + pub + " ms"
        );

        return res;
    }
}
