package manager.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import manager.app.ServiceCaller;
import manager.app.clients.HelloV2;
import manager.app.clients.TestClient;
import manager.app.models.ServiceResult;

@RestController
public class ServiceController {

    private ServiceCaller serviceCaller;

    public ServiceController(ServiceCaller serviceCaller) {
        this.serviceCaller = serviceCaller;
    }

    @GetMapping("/service")
    ServiceResult run() throws Exception {
        long call = 0;
        long pub = 0;
        if (false) {
            call = serviceCaller.call();
            pub = serviceCaller.publish();
        } else {
            call = serviceCaller.callV2();
        }
        var res = new ServiceResult(
                "Total for call: " + call + " ms",
                "Total for publish: " + pub + " ms");

        return res;
    }
}
