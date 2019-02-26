package me.ggamer55.bcm.bukkit;

import me.ggamer55.bcm.basic.Authorizer;
import me.ggamer55.bcm.basic.Namespace;
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
