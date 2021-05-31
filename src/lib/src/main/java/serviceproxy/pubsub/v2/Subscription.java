package serviceproxy.pubsub.v2;

import java.lang.reflect.Method;

public class Subscription {
    public String queue;
    public Method method;

    public Subscription(String queue, Method method) {
        this.queue = queue;
        this.method = method;
    }
}
