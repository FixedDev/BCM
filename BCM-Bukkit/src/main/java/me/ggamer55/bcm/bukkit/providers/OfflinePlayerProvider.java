package me.ggamer55.bcm.bukkit.providers;

import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.parametric.ParameterProvider;
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
    public OfflinePlayer transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        if (arguments.hasNext()) {
            String next = arguments.next();

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

        if (defaultValue == null || defaultValue.trim().isEmpty()) {
            throw new NoMoreArgumentsException(arguments.getSize(), arguments.getPosition());
        }

        CommandSender sender = namespace.getObject(CommandSender.class, "sender");

        if (defaultValue.equalsIgnoreCase("self") && sender instanceof Player) {
            return (OfflinePlayer) sender;
        }

        throw new NoMoreArgumentsException(arguments.getSize(), arguments.getPosition());
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
