package me.fixeddev.bcm.parametric.exceptions;

public class NoTransformerFound extends Exception {
    public NoTransformerFound(Class<?> clazz) {
        super("Failed to get transformer for class " + clazz.getName());
    }
}
