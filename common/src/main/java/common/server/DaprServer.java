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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import common.helpers.ServiceLoader;

public class DaprServer extends AppCallbackGrpc.AppCallbackImplBase {

    @Inject
    DaprServer(Injector injector) {
        ServiceLoader.init(injector);
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
     * It will create an instance of the class that should be called and call it.
     *
     * @param request          Dapr envelope request,
     * @param responseObserver Dapr envelope response.
     */
    @Override
    public void onInvoke(CommonProtos.InvokeRequest request,
            StreamObserver<CommonProtos.InvokeResponse> responseObserver) {
        try {

            var refClass = ServiceLoader.create(request.getMethod());
            var invokeMethod = ServiceLoader.getMethod(request.getMethod(), refClass.getClass());
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
        for (var subscription : ServiceLoader.getSubscriptions()) {
            listTopics.addSubscriptions(TopicSubscription.newBuilder().setPubsubName(subscription.pubsub)
                    .setTopic(subscription.topic).build());
        }
        System.out.println("Listing " + listTopics.getSubscriptionsCount() + " topics.");
        var response = listTopics.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public DaprServer registerServices(Iterable<Class<?>> classes) {
        ServiceLoader.registerServices(classes);
        return this;
    }

    public DaprServer registerSubscribers(String pubsub) {
        ServiceLoader.registerSubscribers(pubsub);
        return this;
    }

    @Override
    public void onTopicEvent(TopicEventRequest request, StreamObserver<TopicEventResponse> responseObserver) {
        try {
            var subscription = ServiceLoader.getSubscriptions().stream().filter(s -> s.topic.equals(request.getTopic())).findFirst()
                    .orElseThrow();
            var refClass = ServiceLoader.create(subscription.method);
            var invokeMethod = ServiceLoader.getMethod(subscription.method, refClass.getClass());

            var returnType = invokeMethod.getParameterTypes()[0];

            var a = returnType.getMethod("newBuilder", null);
            var builder = (Message.Builder) a.invoke(null, null);

            var json = new DefaultObjectSerializer().deserialize(request.getData().toByteArray(), String.class);
            com.google.protobuf.util.JsonFormat.parser().merge(json, builder);

            var event = builder.build();

            // If response is a response wrapper, check status and set status accordingly.
            invokeMethod.invoke(refClass, event);

            var eventResponse = TopicEventResponse.newBuilder().setStatus(TopicEventResponseStatus.SUCCESS).build();
            responseObserver.onNext(eventResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    private Object getRequest(Method method, ByteString request) throws Exception {
        var parseFrom = method.getParameterTypes()[0].getMethod("parseFrom", ByteString.class);
        return parseFrom.invoke(null, request);
    }

}
