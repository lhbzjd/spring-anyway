package ink.anyway.component.common.engine.exception;

import org.springframework.beans.factory.BeanCreationException;

public class ThreadPoolEngineExistsException extends BeanCreationException {

    /**
     * 构造一个线程池引擎已存在异常。
     *
     * @param message
     *            详细消息。
     */
    public ThreadPoolEngineExistsException(String message) {
        super(message);
    }

    /**
     * 构造一个线程池引擎已存在异常。
     *
     * @param message
     *            详细消息。
     * @param cause
     *            原因。
     */
    public ThreadPoolEngineExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
