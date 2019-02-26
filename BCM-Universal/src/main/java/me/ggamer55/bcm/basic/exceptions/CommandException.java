package me.ggamer55.bcm.basic.exceptions;

public class CommandException extends Exception {
    public CommandException(Throwable cause) {
        super("There was an error while executing a command.", cause);
    }
}
