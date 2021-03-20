package common.model;

public class Error {
    private StatusCode _code = StatusCode.Ok;
    private String _error = "";

    public Error(StatusCode code, String error) {
        _code = code;
        _error = error;
    }
    public Error() { }
}
