package common.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Injector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import common.pubsub.Subscriber;
import common.pubsub.Subscription;
import common.server.ExposedService;

public class ServiceLoader {
    private static String type = "spring";

    private static final Logger _logger = LogManager.getLogger(ServiceLoader.class);
    private static Map<String, Class<?>> _services = new HashMap<String, Class<?>>();
    private static ArrayList<Subscription> _subscriptions = new ArrayList<Subscription>();

    private static Injector _injector;

    private static ApplicationContext ctx;

    public static void init(Injector injector, ApplicationContext ctx) {
        _injector = injector;
        ServiceLoader.ctx = ctx;
    }

    public static Object create(String method) throws Exception {
        var className = method.substring(0, method.lastIndexOf("."));
        var invokeClass = _services.get(className.toLowerCase());
        return create(invokeClass);
    }

    public static Object create(Class<?> clazz) {

        if (type != "spring") {
            return _injector.getInstance(clazz);
        } else {
            return ctx.getBean(clazz);
        }
    }

    public static Method getMethod(String methodName, Class<?> invokeClass) throws Exception {
        Method invokeMethod = null;

        if (methodName.contains(".")) {
            methodName = methodName.substring(methodName.lastIndexOf(".") + 1, methodName.length());
        }

        methodName = methodName.toLowerCase();
        for (var method : invokeClass.getMethods()) {
            if (method.getName().toLowerCase().equals(methodName)) {
                invokeMethod = method;
                break;
            }
        }

        if (invokeMethod == null) {
            throw new Exception("Method " + methodName + " not found");
        }
        return invokeMethod;
    }

    public static Map<String, Class<?>> getServices() {
        return _services;
    }

    public static void registerServices(Iterable<Class<?>> classes) {
        for (var clazz : classes) {
            var exposedService = clazz.getAnnotation(ExposedService.class);
            if (exposedService != null) {
                _services.put(clazz.getSimpleName().toLowerCase(), clazz);
            }
        }
        _logger.info("Registered {} services.", _services.size());
    }

    public static ArrayList<Subscription> getSubscriptions() {
        return _subscriptions;
    }

    public static void registerSubscribers(String pubsub) {
        ServiceLoader.getServices().forEach((k, clazz) -> {
            for (var method : clazz.getMethods()) {
                var subscriber = method.getAnnotation(Subscriber.class);
                if (subscriber != null) {
                    var s = new Subscription();
                    s.method = clazz.getSimpleName() + "." + method.getName();
                    s.topic = subscriber.topic();
                    s.pubsub = pubsub;
                    _logger.info("Registering {} on {}", s.topic, s.method);
                    _subscriptions.add(s);
                }
            }
        });
        _logger.info("Registered {} topics", _subscriptions.size());
    }
}
