package me.fixeddev.bcm.basic;

public interface PermissionMessageProvider {
    String getMessage(ICommand command, Namespace namespace);
}
