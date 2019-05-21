package me.fixeddev.bcm.basic.exceptions;

public class NoMoreArgumentsException extends Exception {
    public NoMoreArgumentsException(int size, int position) {
        super("No more arguments were found, size: " + size + " position: " + position);
    }
}
