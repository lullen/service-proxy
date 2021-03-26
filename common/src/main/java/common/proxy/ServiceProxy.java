package common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import com.google.inject.Injector;
import com.google.protobuf.Message;

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

        var sp = BaseServiceProxy.create();

        var packageName = method.getDeclaringClass().getPackageName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));
        var methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        // System.out.println("Ret type: " + method.getGenericReturnType().getTypeName());

        Class<?> returnType = null;
        if (method.getGenericReturnType() instanceof ParameterizedType) {
            var pType = (ParameterizedType) method.getGenericReturnType();
            if (pType.getActualTypeArguments().length != 1) {
                throw new Exception("Only one generic parameter type is allowed");
            }
            var aType = pType.getActualTypeArguments()[0];
            // System.out.println("A type: " + aType.getTypeName());
            if (aType instanceof Class<?>) {
                // System.out.println("A type is class: " + aType.getTypeName());
                returnType = (Class<?>) aType;
            }

            // System.out.println("Ret type: " +
            // method.getGenericReturnType().getTypeName());
        }
        if (returnType == null) {
            throw new Exception("Could not find the generic type of the return type");
        }
        var res = sp.invoke(packageName, methodName, (Message) args[0], returnType);
        return res;
    }

}
