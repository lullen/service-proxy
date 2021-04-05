package common.model;

import java.util.function.Function;

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

    public <TRes> Response<TRes> next(Function<Response<T>, Response<TRes>> request) {
        if (this.hasError()) {
            return new Response<>(this.error);
        } else {
            return request.apply(this);
        }
    }

    public <TRes> Response<TRes> onError(Function<Error, Response<TRes>> request) {
        return request.apply(this.error);
    }
}
