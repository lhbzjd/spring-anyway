package ink.anyway.component.message.exception;

public class MethodNotSupportException extends RuntimeException {

    public MethodNotSupportException() {
        super();
    }

    public MethodNotSupportException(String message) {
        super(message);
    }

    public MethodNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNotSupportException(Throwable cause) {
        super(cause);
    }

}
