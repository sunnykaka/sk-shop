package common.exceptions;

/**
 * ERP项目业务异常
 * User: liubin
 * Date: 14-1-10
 */
public class AppBusinessException extends RuntimeException {

    public AppBusinessException() {
        super();
    }

    public AppBusinessException(String message) {
        super(message);
    }

    public AppBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppBusinessException(Throwable cause) {
        super(cause);
    }

}
