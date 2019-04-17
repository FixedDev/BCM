package me.ggamer55.bcm.basic.exceptions;

import me.ggamer55.bcm.basic.ICommand;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.PermissionMessageProvider;

public abstract class NoOpPermissionMessageProvider implements PermissionMessageProvider {
    @Override
    public String getMessage(ICommand command, Namespace namespace) {
        return command.getPermissionMessage();
    }
}
