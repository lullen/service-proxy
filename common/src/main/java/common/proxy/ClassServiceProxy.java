package common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.google.protobuf.Message;

public class ClassServiceProxy implements InvocationHandler {

    public static <T> T create(Class<T> clazz) {

        var classLoader = ClassServiceProxy.class.getClassLoader();
        var proxy = Proxy.newProxyInstance(classLoader, new Class[] { clazz }, new ClassServiceProxy());
        return (T) proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (args.length != 1) {
            throw new Exception("Only one argument is allowed");
        }

        var sp = ServiceProxy.create();

        var packageName = method.getDeclaringClass().getPackageName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));
        var methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        var res = sp.invoke(packageName, methodName, (Message) args[0], method.getReturnType());
        return res;
    }

}
