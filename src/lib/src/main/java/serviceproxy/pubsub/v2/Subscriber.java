package serviceproxy.pubsub.v2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscriber {
    public static final int DEFAULT_PREFETCH_COUNT = 10;

    String queue();
    String name();
    boolean autoAck() default false;
    int prefetch() default DEFAULT_PREFETCH_COUNT;
}
