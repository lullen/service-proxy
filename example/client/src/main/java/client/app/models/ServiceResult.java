package client.app.models;

public class ServiceResult {
    private String callResult;
    private String publishResult;

    public ServiceResult(String callResult, String publishResult) {
        this.callResult = callResult;
        this.publishResult = publishResult;
    }

    public String getCallResult(){
        return callResult;
    }

    public String getPublishResult(){
        return publishResult;
    }
}
