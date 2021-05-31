package serviceproxy.pubsub.v2;

public interface PubSubListener {
    
    void start();
    void stop();

    void registerSubscriptions(Class<?> clazz);
}
