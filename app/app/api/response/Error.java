package api.response;

import common.exceptions.ErrorCode;

import java.util.Optional;

/**
 * Created by liubin on 15-8-3.
 */
public class Error {

    private String code;

    private String message;

    private String requestUri;

    public Error() {
    }

    public Error(ErrorCode errorCode, Optional<String> message, String requestUri) {
        this.code = errorCode.getName();
        this.message = message.orElse(errorCode.message);
        this.requestUri = requestUri;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestUri() {
        return requestUri;
    }
}
