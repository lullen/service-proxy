package serviceproxy.proxy;

public interface EventPublisherProvider {
    void publish(String pubsubName, String topic, Object request) throws Exception;
}