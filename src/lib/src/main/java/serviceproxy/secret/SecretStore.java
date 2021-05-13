package serviceproxy.secret;

import serviceproxy.proxy.BaseServiceProxy;

public class SecretStore {
    public static String get(String secretStoreName, String key) throws Exception {
        return BaseServiceProxy.create().getSecret(secretStoreName, key);
    }
}
