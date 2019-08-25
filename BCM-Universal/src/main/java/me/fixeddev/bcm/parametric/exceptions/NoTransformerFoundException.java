package me.fixeddev.bcm.parametric.exceptions;

import me.fixeddev.bcm.basic.exceptions.CommandException;

public class NoTransformerFoundException extends CommandException {
    public NoTransformerFoundException(Class<?> clazz) {
        super("Failed to get transformer for class " + clazz.getName());
    }

    public NoTransformerFoundException(Class<?> clazz, Class<?> annotation) {
        super("Failed to get transformer for class " + clazz.getName() + " annotated with " + annotation.getName());
    }
}
