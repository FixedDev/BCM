package me.fixeddev.bcm.basic.exceptions;

public class ArgumentsParseException extends Exception {
    public ArgumentsParseException(String argument, Class<?> type) {
        super("Failed to parse argument: " + argument + " type: " + type.getName());
    }

    public ArgumentsParseException(String argument, Class<?> type, String message) {
        super("Failed to parse argument: " + argument + " type: " + type.getName() + " error message: " + message);
    }

    public ArgumentsParseException(String argument, Class<?> type, Class<?> annotationType) {
        super("Failed to parse argument: " + argument + " type: " + type.getName() + " annotated with: " + annotationType.getName());
    }

    public ArgumentsParseException(String argument, Class<?> type, Class<?> annotationType, String message) {
        super("Failed to parse argument: " + argument + " type: " + type.getName() + " annotated with: " + annotationType.getName() + " error message: " + message);
    }
}
