package serviceproxy.proxy;

public class SecretStore {
    public static void init(String secretStore) {
        BaseServiceProxy.initSecrets(secretStore);
    }

    public static String get(String key) throws Exception {
        return BaseServiceProxy.create().getSecret(key);
    }
}
