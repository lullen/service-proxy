// package serviceproxy.proxy;

// import com.google.inject.AbstractModule;
// import com.google.inject.multibindings.Multibinder;

// import serviceproxy.proxy.middleware.ErrorResultMiddleware;
// import serviceproxy.proxy.middleware.LoggingMiddleware;
// import serviceproxy.proxy.middleware.ProxyMiddleware;
// import serviceproxy.proxy.middleware.TimingMiddleware;

// public class ProxyModule extends AbstractModule {

//     @Override
//     protected void configure() {

//         var multiBinder = Multibinder.newSetBinder(binder(), ProxyMiddleware.class);
//         multiBinder.addBinding().to(ErrorResultMiddleware.class);
//         multiBinder.addBinding().to(TimingMiddleware.class);
//         multiBinder.addBinding().to(LoggingMiddleware.class);

//         bind(BaseServiceProxy.class);
//     }
// }
