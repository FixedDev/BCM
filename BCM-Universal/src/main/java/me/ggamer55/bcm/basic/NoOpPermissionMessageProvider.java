package me.ggamer55.bcm.basic;

public class NoOpPermissionMessageProvider implements PermissionMessageProvider {
    @Override
    public String getMessage(ICommand command, Namespace namespace) {
        return command.getPermissionMessage();
    }
}
