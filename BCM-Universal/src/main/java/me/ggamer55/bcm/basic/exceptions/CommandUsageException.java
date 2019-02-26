package me.ggamer55.bcm.basic.exceptions;

import me.ggamer55.bcm.basic.ICommand;

public class CommandUsageException extends Exception{

    private ICommand command;

    public CommandUsageException(String usageMessage, ICommand command) {
        super(usageMessage);
        this.command = command;
    }

    public ICommand getCommand() {
        return command;
    }
}
