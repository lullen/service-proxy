package serviceproxy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import com.google.protobuf.Message;

import org.springframework.stereotype.Component;

import serviceproxy.server.ExposedService;

@Component
public class ServiceProxy implements InvocationHandler {

    private DaprProxy daprProxy;
    
    private InProcProxy inProcProxy;
    private static ProxyType type;

    // Maybe inject IServiceProxy and InProcProxy instead?
    // IServiceProxy will be Dapr / InProc and InProc will be used for legacy
    public ServiceProxy(DaprProxy daprProxy, InProcProxy inProcProxy) {
        this.daprProxy = daprProxy;
        this.inProcProxy = inProcProxy;

    }


    public static void init(ProxyType type) {
        ServiceProxy.type = type;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {

        var classLoader = ServiceProxy.class.getClassLoader();
        var proxy = Proxy.newProxyInstance(classLoader, new Class[] { clazz }, this);
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


        var serviceAnnotation = method.getDeclaringClass().getAnnotation(ExposedService.class);
        if (type == ProxyType.Dapr && !serviceAnnotation.legacy()) {
            return daprProxy.invoke(packageName, methodName, (Message) args[0], returnType);
        } else {
            return inProcProxy.invoke(packageName, methodName, (Message) args[0], returnType);
        }
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
