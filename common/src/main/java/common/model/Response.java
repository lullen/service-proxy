package common.model;

public class Response<T> {
    public Error error = new Error();
    public T result;

    public Response() {

    }

    public Response(T result) {
        this.result = result;
    }

    public Response(Error error) {
        this.error = error;
    }

    public Boolean hasError() {
        return error.hasError();
    }
}
