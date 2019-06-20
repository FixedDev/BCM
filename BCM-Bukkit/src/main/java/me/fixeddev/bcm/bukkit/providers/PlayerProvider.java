package me.fixeddev.bcm.bukkit.providers;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.ParameterProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerProvider implements ParameterProvider<Player> {
    @Override
    public Player transformParameter(ArgumentStack arguments, Namespace namespace, Annotation modifier, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        if (arguments.hasNext()) {
            String next = arguments.next();

            Player player = Bukkit.getPlayer(next);

            if (player == null) {
                try {
                    player = Bukkit.getPlayer(UUID.fromString(next));
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
            return (Player) sender;
        }

        throw new NoMoreArgumentsException(arguments.getSize(), arguments.getPosition());
    }

    @Override
    public List<String> getSuggestions(String text, Namespace namespace) {
        return Bukkit.getServer().matchPlayer(text).stream().filter(player -> {
            CommandSender sender = namespace.getObject(CommandSender.class, "sender");

            if(sender instanceof Player){
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
