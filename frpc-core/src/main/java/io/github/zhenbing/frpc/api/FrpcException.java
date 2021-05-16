package io.github.zhenbing.frpc.api;

/**
 * FrpcException
 *
 * @author fengzhenbing
 */
public class FrpcException extends RuntimeException {
    private static final long serialVersionUID = -948934144333391209L;

    /**
     * Instantiates a new Frpc exception.
     */
    public FrpcException() {
    }

    /**
     * Instantiates a new Frpc exception.
     *
     * @param message the message
     */
    public FrpcException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Frpc exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public FrpcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Frpc exception.
     *
     * @param cause the cause
     */
    public FrpcException(final Throwable cause) {
        super(cause);
    }
}
