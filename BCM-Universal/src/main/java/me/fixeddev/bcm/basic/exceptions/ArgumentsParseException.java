package me.fixeddev.bcm.basic.exceptions;

public class ArgumentsParseException extends Exception {
    public ArgumentsParseException(String argument, Class<?> type) {
        super("Failed to parse argument: " + argument + " type: " + type.getName());
    }
}
