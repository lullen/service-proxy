package serviceproxy.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import serviceproxy.proxy.ProxyType;
import serviceproxy.pubsub.Subscriber;
import serviceproxy.pubsub.Subscription;
import serviceproxy.server.ExposedService;

@Component
public class ServiceLoader {
    private ApplicationContext applicationContext;

    private static final Logger _logger = LogManager.getLogger(ServiceLoader.class);
    private static Map<String, Class<?>> _services = new HashMap<String, Class<?>>();
    private static ArrayList<Subscription> _subscriptions = new ArrayList<Subscription>();

    public ServiceLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object create(String method) throws Exception {
        var className = method.substring(0, method.lastIndexOf("."));
        var invokeClass = _services.get(className.toLowerCase());
        if (invokeClass == null) {
            return null;
        }
        return applicationContext.getAutowireCapableBeanFactory().getBean(invokeClass);
    }

    public Method getMethod(String methodName, Class<?> invokeClass) throws Exception {
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

    public static void registerServices(ApplicationContext ctx, ProxyType type) {
        var serviceDictionary = ctx.getBeansWithAnnotation(ExposedService.class);
        var services = serviceDictionary
            .values()
            .stream()
            .flatMap(b -> Arrays.stream(b.getClass().getInterfaces()))
            .collect(Collectors.toUnmodifiableList());
        ServiceLoader.registerServices(services, type);
    }

    public static void registerServices(Iterable<? extends Class<?>> classes, ProxyType type) {

        for (var clazz : classes) {
            var exposedService = clazz.getAnnotation(ExposedService.class);
            if (exposedService != null) {
                String className;
                if (type == ProxyType.InProc) {
                    className = clazz.getName();
                } else {
                    className = clazz.getSimpleName();
                }
                className = className.toLowerCase();

                if (_services.containsKey(className)) {
                    _logger.warn("Overwriting {} as it has already been added.", className);
                }
                _services.put(className, clazz);
            }
        }
        _logger.info("Registered {} services.", _services.size());

        registerSubscribers(type);
    }

    public static ArrayList<Subscription> getSubscriptions() {
        return _subscriptions;
    }

    private static void registerSubscribers(ProxyType type) {
        ServiceLoader.getServices().forEach((k, clazz) -> {
            for (var method : clazz.getMethods()) {
                var subscriber = method.getAnnotation(Subscriber.class);
                if (subscriber != null) {
                    var s = new Subscription();
                    s.method = String.format("%s.%s", getServiceClassName(clazz, type), method.getName());
                    s.topic = subscriber.topic();
                    s.pubsub = subscriber.name();
                    s.legacy = subscriber.legacy();
                    _logger.info("Registering {} on {}", s.topic, s.method);
                    _subscriptions.add(s);
                }
            }
        });
        _logger.info("Registered {} topics", _subscriptions.size());
    }

    private static String getServiceClassName(Class<?> clazz, ProxyType type) {
        String className;
        if (type == ProxyType.InProc) {
            className = clazz.getName();
        } else {
            className = clazz.getSimpleName();
        }
        return className;
    }
}
