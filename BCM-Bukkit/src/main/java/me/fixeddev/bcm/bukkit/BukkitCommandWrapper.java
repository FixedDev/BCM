package me.fixeddev.bcm.bukkit;

import me.fixeddev.bcm.basic.CommandDispatcher;
import me.fixeddev.bcm.basic.ICommand;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandUsageException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;

public class BukkitCommandWrapper extends Command {

    private ICommand command;

    private CommandDispatcher dispatcher;

    public BukkitCommandWrapper(ICommand command, CommandDispatcher dispatcher) {
        super(command.getNames()[0]);

        if (command.getNames().length > 1) {
            String[] aliases = Arrays.copyOfRange(command.getNames(), 1, command.getNames().length - 1);

            this.setAliases(new ArrayList<>(Arrays.asList(aliases)));
        }

        this.setDescription(command.getDescription());
        this.setUsage(command.getUsage());

        this.setPermission(command.getPermission());
        this.setPermissionMessage(command.getPermissionMessage());

        this.command = command;
        this.dispatcher = dispatcher;
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        StringJoiner joiner = new StringJoiner(" ");

        joiner.add(this.getName());

        for (String arg : args) {
            joiner.add(arg);
        }

        Namespace namespace = new Namespace();
        namespace.setObject(CommandSender.class, "sender", commandSender);

        try {
            if (dispatcher.dispatchCommand(namespace, joiner.toString())) {
                return true;
            }

        } catch (me.fixeddev.bcm.basic.exceptions.CommandException ex) {
            String exceptionMessage = ex.getMessage();

            if (exceptionMessage != null && !exceptionMessage.isEmpty()) {
                commandSender.sendMessage(exceptionMessage);
            }

            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        } catch (NoPermissionsException ex) {
            commandSender.sendMessage(ChatColor.RED + ex.getMessage());

            return true;
        } catch (ArgumentsParseException ex) {
            String message = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', ex.getMessage());

            String[] splittedMessage = message.split("\n");
            splittedMessage[0] = ChatColor.RED + "Error: " + splittedMessage[0];


            for (String s : splittedMessage) {
                commandSender.sendMessage(s);
            }

            return true;
        } catch (CommandUsageException ex) {
            String message = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', ex.getMessage());

            String[] splittedMessage = message.split("\n");
            splittedMessage[0] = ChatColor.RED + "Usage: " + splittedMessage[0];

            for (String s : splittedMessage) {
                commandSender.sendMessage(s);
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        List<String> completions = null;
        try {
            if (command != null) {
                StringJoiner joiner = new StringJoiner(" ");

                joiner.add(alias);

                for (String arg : args) {
                    joiner.add(arg);
                }

                Namespace namespace = new Namespace();
                namespace.setObject(CommandSender.class, "sender", sender);

                try {
                    completions = dispatcher.getCommandSuggestions(namespace, joiner.toString());
                } catch (NoPermissionsException ex) {
                    sender.sendMessage(ex.getMessage());
                } catch (NoMoreArgumentsException ex) {
                    String message = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', ex.getMessage());

                    String[] splittedMessage = message.split("\n");
                    splittedMessage[0] = ChatColor.RED + "Error: " + splittedMessage[0];

                    for (String s : splittedMessage) {
                        sender.sendMessage(s);
                    }
                }
            }
        } catch (Throwable ex) {
            StringBuilder message = new StringBuilder();

            message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');

            for (String arg : args) {
                message.append(arg).append(' ');
            }

            throw new CommandException(message.toString(), ex);
        }

        if (completions == null) {
            return super.tabComplete(sender, alias, args);
        }

        return completions;
    }
}
