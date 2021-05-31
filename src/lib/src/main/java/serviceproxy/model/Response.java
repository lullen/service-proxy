package serviceproxy.model;

import java.util.function.Function;

public class Response<T> {
    public Error error = new Error();

    public T result;

    public Response() {}

    public Response(T result) {
        this.result = result;
    }

    public Response(Error error) {
        this.error = error;
    }

    public Boolean hasError() {
        return error.hasError();
    }

    public <TRes> Response<TRes> then(Function<Response<T>, Response<TRes>> request) {
        if (this.hasError()) {
            return new Response<>(this.error);
        }
        return request.apply(this);
    }

    public Response<T> onError(Function<Error, Response<T>> request) {
        if (!this.hasError()) {
            return this;
        }
        return request.apply(this.error);
    }


    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
