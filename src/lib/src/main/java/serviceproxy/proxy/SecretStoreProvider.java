package serviceproxy.proxy;

public interface SecretStoreProvider {
    String getSecret(String secretStoreName, String key) throws Exception;
}