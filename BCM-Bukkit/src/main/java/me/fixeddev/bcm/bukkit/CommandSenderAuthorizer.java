package me.fixeddev.bcm.bukkit;

import me.fixeddev.bcm.basic.Authorizer;
import me.fixeddev.bcm.basic.Namespace;
import org.bukkit.command.CommandSender;

public class CommandSenderAuthorizer implements Authorizer {
    @Override
    public boolean isAuthorized(Namespace namespace, String permission) {
        CommandSender sender = namespace.getObject(CommandSender.class, "sender");

        if (sender == null) {
            return false;
        }

        return sender.hasPermission(permission);
    }
}
