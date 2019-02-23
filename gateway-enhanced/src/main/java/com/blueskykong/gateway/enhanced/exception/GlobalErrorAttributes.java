package com.blueskykong.gateway.enhanced.exception;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author keets
 * @data 2018/12/11.
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public GlobalErrorAttributes() {
        super(false);
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return assembleError(request);
    }

    private Map<String, Object> assembleError(ServerRequest request) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        if (error instanceof Exception) {
            errorAttributes.put("code", ((ServerException) error).getCode().getCode());
            errorAttributes.put("data", error.getMessage());

        } else {
            errorAttributes.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            errorAttributes.put("data", "GATEWAY ERROR");
        }
        return errorAttributes;
    }

    /**
     * @return the status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}