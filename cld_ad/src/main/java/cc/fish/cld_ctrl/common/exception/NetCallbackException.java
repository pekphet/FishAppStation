package cc.fish.cld_ctrl.common.exception;

/**
 * Created by fish on 16-12-14.
 */

public class NetCallbackException extends Exception {
    public NetCallbackException() {
    }

    public NetCallbackException(String message) {
        super(message);
    }

    public NetCallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetCallbackException(Throwable cause) {
        super(cause);
    }
}
