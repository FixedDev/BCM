package me.fixeddev.bcm.basic.exceptions;

public class CommandUsageException extends Exception {
    public CommandUsageException() {
    }

    public CommandUsageException(String message) {
        super(message);
    }

    public CommandUsageException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandUsageException(Throwable cause) {
        super(cause);
    }

    public CommandUsageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
