package me.ggamer55.bcm.basic;

import java.util.Optional;

public interface CommandRegistry {
    void registerCommand(ICommand command);

    void unregisterCommand(ICommand command);

    void unregisterAllCommands();

    Optional<ICommand> getCommand(String alias);

    Optional<ICommand> getCommandFromCommandLine(String commandLine);

    boolean isCommandRegistered(String alias);

}
