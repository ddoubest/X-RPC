package org.chike.rpc.core.exceptions;

public class ExtensionClashException extends RuntimeException {
    private static final long serialVersionUID = -8997647396133915610L;

    public ExtensionClashException() {
        super();
    }

    public ExtensionClashException(String message) {
        super(message);
    }

    public ExtensionClashException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionClashException(Throwable cause) {
        super(cause);
    }

    protected ExtensionClashException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
