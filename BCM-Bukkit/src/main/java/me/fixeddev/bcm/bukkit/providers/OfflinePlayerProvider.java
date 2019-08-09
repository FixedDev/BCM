package me.fixeddev.bcm.bukkit.providers;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OfflinePlayerProvider implements ParameterProvider<OfflinePlayer> {
    @Override
    public OfflinePlayer transformParameter(ArgumentStack arguments, Namespace namespace, Annotation modifier) throws NoMoreArgumentsException, ArgumentsParseException {
        String next = arguments.next();

        CommandSender sender = namespace.getObject(CommandSender.class,"sender");

        if(next.equals("self") && sender instanceof Player){
            return (OfflinePlayer) namespace.getObject(CommandSender.class,"sender");
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(next);

        if (player == null) {
            try {
                player = Bukkit.getOfflinePlayer(UUID.fromString(next));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return player;
    }

    @Override
    public List<String> getSuggestions(String text, Namespace namespace) {
        return Bukkit.getServer().matchPlayer(text).stream().filter(player -> {
            CommandSender sender = namespace.getObject(CommandSender.class, "sender");

            if (sender instanceof Player) {
                Player playerSender = (Player) sender;

                return playerSender.canSee(player);
            }

            return true;
        }).map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public boolean isProvided() {
        return true;
    }
}
