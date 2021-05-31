package serviceproxy.model;

public class Error {
    private StatusCode code = StatusCode.Ok;
    private String errorMessage = "";

    public Error(StatusCode code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public Error() {
    }

    public StatusCode getStatusCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean hasError() {
        return code != StatusCode.Ok;
    }
}
