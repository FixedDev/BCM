package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.basic.ICommand;

import java.util.Optional;

interface CommandTreeResult {
    /**
     * The content of this optional isn't present if the command can't be created due to different reasons
     * @return - An Optional instance of a command
     */
    Optional<ICommand> commandResult();

    /**
     * @return - A boolean indicating if any sub command was registered on the command tree
     */
    boolean subCommandsRegistered();
}
