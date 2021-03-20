package client.app;

import com.budbee.proto.HelloRequest;
import com.budbee.proto.HelloResponse;

import common.proxy.ServiceProxy;

public class ServiceCaller {
    public long call() throws Exception {
        System.out.println("Howdy from call");
        var count = 0;
        var start = System.currentTimeMillis();
        var sp = ServiceProxy.create();
        while (count < 10) {
            var request = 
                HelloRequest
                    .newBuilder()
                    .setText("Hello there from call #" + count++ + "!")
                    .setNewText("What's up?")
                    .setOtherText("Alright")
                .build();
            System.out.println(request.getText() + " " + request.getNewText());

            var resp = sp.invoke("server", "Hello.hello", request, HelloResponse.class);
            // System.out.println("Response: " + resp.getText() + " " + resp.getOtherText());
        }
        System.out.println("Total: " + (System.currentTimeMillis() - start) + " ms");
        return System.currentTimeMillis() - start;
    }

    public long publish() throws Exception {
        System.out.println("Howdy from proxy");
        var count = 0;
        var start = System.currentTimeMillis();
        var client = ServiceProxy.create();

        while (count < 1) {
            // var request = new HelloEvent();
            // request.hello = "Hello mr event #" + count++;
            var request = HelloRequest.newBuilder().setText("Hello there from publish #" + count++ + "!").setNewText("What's up?")
                    .setOtherText("Alright").build();

            System.out.println(request.getText() + " " + request.getNewText());
            client.publish("hello", request);
            // System.out.println("Published hello with text: " + request.getText());

            // Thread.sleep(500);
        }
        System.out.println("Total: " + (System.currentTimeMillis() - start) + " ms");
        return System.currentTimeMillis() - start;
    }
}
