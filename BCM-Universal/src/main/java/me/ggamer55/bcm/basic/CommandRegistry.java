package me.ggamer55.bcm.basic;

import java.util.Optional;

public interface CommandRegistry {
    void registerCommand(ICommand command);

    void unregisterCommand(ICommand command);

    void unregisterAllCommands();

    Optional<ICommand> getCommand(String alias);

    boolean isCommandRegistered(String alias);
}
