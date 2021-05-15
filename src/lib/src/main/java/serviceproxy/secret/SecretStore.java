package serviceproxy.secret;

import org.springframework.stereotype.Component;

import serviceproxy.proxy.DaprProxy;
import serviceproxy.proxy.InProcProxy;
import serviceproxy.proxy.ProxyType;

@Component
public class SecretStore {


    private static ProxyType type;
    private InProcProxy inProcProxy;
    private DaprProxy daprProxy;

    // Maybe inject IServiceProxy and InProcProxy instead?
    // IServiceProxy will be Dapr / InProc and InProc will be used for legacy
    public SecretStore(DaprProxy daprProxy, InProcProxy inProcProxy){
        this.daprProxy = daprProxy;
        this.inProcProxy = inProcProxy;
        
    }

    public static void init(ProxyType type) {
        SecretStore.type = type;
    }

    public String get(String secretStoreName, String key) throws Exception {

        if (SecretStore.type == ProxyType.Dapr) {
            return daprProxy.getSecret(secretStoreName, key);
        } else {
            return inProcProxy.getSecret(secretStoreName, key);
        }
    }
}
