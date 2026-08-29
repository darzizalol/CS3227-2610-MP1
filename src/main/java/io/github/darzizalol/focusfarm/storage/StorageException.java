package io.github.darzizalol.focusfarm.storage;

/** Signals that farm data could not be read or written safely. */
public class StorageException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates a storage exception with a user-facing message and cause. */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
