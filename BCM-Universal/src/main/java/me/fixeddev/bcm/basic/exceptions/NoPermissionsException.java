package me.fixeddev.bcm.basic.exceptions;

public class NoPermissionsException extends Exception {
    public NoPermissionsException() {
    }

    public NoPermissionsException(String message) {
        super(message);
    }
}
