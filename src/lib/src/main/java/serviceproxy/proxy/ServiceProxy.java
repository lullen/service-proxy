package serviceproxy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import com.google.inject.Injector;
import com.google.protobuf.Message;

import serviceproxy.server.ExposedService;

public class ServiceProxy implements InvocationHandler {
    public static void init(ProxyType type, Injector injector) {
        BaseServiceProxy.initProxy(type, injector);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {

        var classLoader = ServiceProxy.class.getClassLoader();
        var proxy = Proxy.newProxyInstance(classLoader, new Class[] { clazz }, new ServiceProxy());
        return (T) proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args.length != 1) {
            throw new Exception("Only one argument is allowed");
        }
        
        var packageName = method.getDeclaringClass().getPackageName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        var methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        Class<?> returnType = getReturnType(method);

        var sp = BaseServiceProxy.create();
        var serviceAnnotation = method.getDeclaringClass().getAnnotation(ExposedService.class);
        var res = sp.invoke(packageName, methodName, (Message) args[0], serviceAnnotation.legacy(), returnType);
        return res;
    }

    private Class<?> getReturnType(Method method) throws Exception {
        Class<?> returnType = null;
        if (method.getGenericReturnType() instanceof ParameterizedType) {
            var pType = (ParameterizedType) method.getGenericReturnType();
            if (pType.getActualTypeArguments().length != 1) {
                throw new Exception("Only one generic parameter type is allowed");
            }
            var aType = pType.getActualTypeArguments()[0];
            if (aType instanceof Class<?>) {
                returnType = (Class<?>) aType;
            }
        }
        if (returnType == null) {
            throw new Exception("Could not find the generic type of the return type");
        }
        return returnType;
    }

}
