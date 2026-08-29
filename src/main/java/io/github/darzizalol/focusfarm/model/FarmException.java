package io.github.darzizalol.focusfarm.model;

/** Signals invalid commands or invalid farm state transitions. */
public class FarmException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing message.
     *
     * @param message explanation suitable for display
     */
    public FarmException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing message and cause.
     *
     * @param message explanation suitable for display
     * @param cause underlying failure
     */
    public FarmException(String message, Throwable cause) {
        super(message, cause);
    }
}
