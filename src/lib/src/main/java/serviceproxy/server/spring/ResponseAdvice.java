package serviceproxy.server.spring;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import serviceproxy.model.Response;
import serviceproxy.model.StatusCode;

@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.getParameterType().isAssignableFrom(Response.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        var responseWrapper = (Response<?>) body;
        if (responseWrapper.hasError()) {
            response.setStatusCode(mapStatus(responseWrapper.error.getStatusCode()));
            return null;
        } else {
            return responseWrapper.result;
        }
    }

    private HttpStatus mapStatus(StatusCode status) {
        switch (status) {
            case Ok:
                return HttpStatus.OK;
            case AlreadyExists:
                return HttpStatus.CONFLICT;
            case InvalidInput:
                return HttpStatus.BAD_REQUEST;
            case NotFound:
                return HttpStatus.NOT_FOUND;
            case Unauthorized:
                return HttpStatus.FORBIDDEN;
            case Exception:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
