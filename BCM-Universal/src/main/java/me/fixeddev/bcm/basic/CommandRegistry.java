package me.fixeddev.bcm.basic;

import java.util.Optional;

public interface CommandRegistry {
    void registerCommand(ICommand command);

    void unregisterCommand(ICommand command);

    void unregisterAllCommands();

    Optional<ICommand> getCommand(String alias);

    Optional<CommandSearchResult> getCommandFromCommandLine(String commandLine);

    boolean isCommandRegistered(String alias);

    interface CommandSearchResult {
        ICommand getCommand();

        String[] getNewArguments();

        String getLabel();
    }
}
