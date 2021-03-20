package common.server;

import io.dapr.serializer.DefaultObjectSerializer;
import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.CommonProtos;
import io.dapr.v1.DaprAppCallbackProtos.*;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventResponse.TopicEventResponseStatus;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.api.ResourceReference.Builder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;

import common.pubsub.Subscription;
import common.pubsub.Subscriber;

public class DaprServer extends AppCallbackGrpc.AppCallbackImplBase {
    private ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
    private Map<String, Class<?>> services = new HashMap<String, Class<?>>();
    private Injector _injector;

    @Inject
    DaprServer(Injector injector) {
        _injector = injector;
    }

    /**
     * Server mode: Grpc server.
     */
    private Server server;

    /**
     * Server mode: starts listening on given port.
     *
     * @param port Port to listen on.
     * @throws IOException Errors while trying to start service.
     */
    public DaprServer start(int port) throws IOException {

        this.server = ServerBuilder.forPort(port).addService(this).build().start();
        System.out.println("Hello to dapr!");
        System.out.printf("Server: started listening on port %d\n", port);

        // Now we handle ctrl+c (or any other JVM shutdown)
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.out.println("Server: shutting down gracefully ...");
                DaprServer.this.server.shutdown();
                System.out.println("Server: Bye.");
            }
        });
        return this;
    }

    /**
     * Server mode: waits for shutdown trigger.
     *
     * @throws InterruptedException Propagated interrupted exception.
     */
    public void awaitTermination() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }

    /**
     * Server mode: this is the Dapr method to receive Invoke operations via Grpc.
     *
     * @param request          Dapr envelope request,
     * @param responseObserver Dapr envelope response.
     */
    @Override
    public void onInvoke(CommonProtos.InvokeRequest request,
            StreamObserver<CommonProtos.InvokeResponse> responseObserver) {
        try {

            var refClass = createClass(request.getMethod());
            var invokeMethod = getMethod(request.getMethod(), refClass.getClass());
            var innerRequest = getRequest(invokeMethod, request.getData().getValue());

            var response = (com.google.protobuf.Message) invokeMethod.invoke(refClass, innerRequest);

            var responseBuilder = CommonProtos.InvokeResponse.newBuilder();
            responseBuilder.setData(Any.pack(response));
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void listTopicSubscriptions(Empty request, StreamObserver<ListTopicSubscriptionsResponse> responseObserver) {
        var listTopics = ListTopicSubscriptionsResponse.newBuilder();
        for (var subscription : subscriptions) {
            listTopics.addSubscriptions(TopicSubscription.newBuilder().setPubsubName(subscription.pubsub)
                    .setTopic(subscription.topic).build());
        }
        System.out.println("Listing " + listTopics.getSubscriptionsCount() + " topics");
        var response = listTopics.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public DaprServer registerServices(Iterable<Class<?>> classes) {
        for (var clazz : classes) {
            var exposedService = clazz.getAnnotation(ExposedService.class);
            if (exposedService != null) {
                services.put(clazz.getSimpleName().toLowerCase(), clazz);
            }
        }
        System.out.println("Registered " + subscriptions.size() + " topics");
        return this;
    }

    public DaprServer registerSubscribers(String pubsub) {
        services.forEach((k, clazz) -> {
            for (var method : clazz.getMethods()) {
                var subscriber = method.getAnnotation(Subscriber.class);
                if (subscriber != null) {
                    var s = new Subscription();
                    s.method = clazz.getSimpleName() + "." + method.getName();
                    s.topic = subscriber.topic();
                    s.pubsub = pubsub;
                    System.out.println("Registering " + s.topic + " on " + s.method);
                    subscriptions.add(s);
                }
            }
        });
        System.out.println("Registered " + subscriptions.size() + " topics");
        return this;
    }

    @Override
    public void onTopicEvent(TopicEventRequest request, StreamObserver<TopicEventResponse> responseObserver) {
        // System.out.println("Got topic event: " + request.getTopic());
        try {
            var subscription = subscriptions.stream().filter(s -> s.topic.equals(request.getTopic())).findFirst()
                    .orElseThrow();
            var refClass = createClass(subscription.method);
            var invokeMethod = getMethod(subscription.method, refClass.getClass());

            var returnType = invokeMethod.getParameterTypes()[0];
            
            var a = returnType.getMethod("newBuilder", null);
            var builder = (Message.Builder)a.invoke(null, null);

            var json = new DefaultObjectSerializer().deserialize(request.getData().toByteArray(), String.class);
            // var builder = request.newBuilderForType();
            com.google.protobuf.util.JsonFormat.parser().merge(json, builder);
            var event = builder.build();
            // var event = new DefaultObjectSerializer().deserialize(request.getData().toByteArray(), returnType);

            // If response is a response wrapper, check status and set status accordingly.
            // var response = (com.google.protobuf.Message) invokeMethod.invoke(refClass,
            // innerRequest);
            invokeMethod.invoke(refClass, event);

            var eventResponse = TopicEventResponse.newBuilder().setStatus(TopicEventResponseStatus.SUCCESS).build();
            responseObserver.onNext(eventResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    private Method getMethod(String methodName, Class<?> invokeClass) throws Exception {
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

    private Object getRequest(Method method, ByteString request) throws Exception {
        var parseFrom = method.getParameterTypes()[0].getMethod("parseFrom", ByteString.class);
        return parseFrom.invoke(null, request);
    }

    private Object createClass(String method) throws Exception {
        var className = method.substring(0, method.lastIndexOf("."));
        // var invokeClass = Class.forName(className);
        var invokeClass = services.get(className.toLowerCase());
        var refClass = _injector.getInstance(invokeClass);

        // var refClass = invo keClass.getDeclaredConstructor().newInstance();

        return refClass;
    }
}
