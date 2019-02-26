package me.ggamer55.bcm.basic;

import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.CommandException;
import me.ggamer55.bcm.basic.exceptions.CommandUsageException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.basic.exceptions.NoPermissionsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicCommandHandler implements CommandRegistry, CommandDispatcher {

    private final Authorizer authorizer;

    private List<ICommand> commands;

    protected Logger logger;

    public BasicCommandHandler(Authorizer authorizer, Logger logger) {
        this.authorizer = authorizer;

        commands = new CopyOnWriteArrayList<>();

        this.logger = logger;
    }

    @Override
    public Authorizer getAuthorizer() {
        return authorizer;
    }

    @Override
    public CommandRegistry getRegistry() {
        return this;
    }

    @Override
    public boolean dispatchCommand(Namespace namespace, String commandLine) throws CommandException, NoPermissionsException, CommandUsageException, ArgumentsParseException {
        String[] args = new String[0];
        Optional<ICommand> found = Optional.empty();
        String label = "";

        for (ICommand commandData : this.commands) {
            for (String alias : commandData.getNames()) {
                if ((commandLine.length() == alias.length() && commandLine.toLowerCase().startsWith(alias.toLowerCase()))
                        || commandLine.toLowerCase().startsWith(alias.toLowerCase() + " ")) {
                    found = Optional.of(commandData);
                    label = alias;
                    if (commandLine.length() > alias.length() + 1) {
                        args = commandLine.substring(alias.length() + 1).split(" ");
                    }
                    break;
                }
            }
        }

        if (!found.isPresent()) {
            return false;
        }

        ICommand command = found.get();

        if (!command.getSubCommands().isEmpty()) {
            Optional<ICommand> subCommandFound = Optional.empty();
            String[] subCommandArgs = new String[0];

            String subCommandLabel = "";

            for (ICommand commandData : command.getSubCommands()) {
                for (String alias : commandData.getNames()) {
                    if ((commandLine.length() == (label.length() + alias.length() + 1) && commandLine.toLowerCase().startsWith(label + " " + alias.toLowerCase()))
                            || commandLine.toLowerCase().startsWith(label + " " + alias.toLowerCase() + " ")) {
                        subCommandFound = Optional.of(commandData);
                        subCommandLabel = alias;
                        if (commandLine.length() > label.length() + alias.length() + 2) {
                            subCommandArgs = commandLine.substring(label.length() + 1).substring(alias.length() + 1).split(" ");
                        }
                        break;
                    }
                }
            }

            if (subCommandFound.isPresent()) {
                command = subCommandFound.get();

                args = subCommandArgs;

                label = label + " " + subCommandLabel;
            }

        }

        if (!command.getPermission().trim().isEmpty() && !authorizer.isAuthorized(namespace, command.getPermission())) {
            throw new NoPermissionsException(command.getPermissionMessage());
        }

        ArgumentArray arguments = new ArgumentArray(args);
        namespace.setObject(String.class, "label", label);

        boolean usage;

        try {
            usage = command.run(namespace, arguments);
        } catch (ArgumentsParseException ex) {
            throw ex;
        } catch (CommandException ex) {
            if (ex.getCause() == null) {
                throw ex;
            }

            if (!(ex.getCause() instanceof NoMoreArgumentsException)) {
                throw ex;
            }

            usage = false;

        } catch (Exception ex) {
            throw new CommandException(ex);
        }

        if (!usage && !command.getUsage().isEmpty()) {
            throw new CommandUsageException(command.getUsage().replace("<command>", label), command);
        }

        return true;
    }

    @Override
    public List<String> getCommandSuggestions(Namespace namespace, String commandLine) throws CommandException, NoMoreArgumentsException, NoPermissionsException {
        String[] args = new String[0];
        Optional<ICommand> found = Optional.empty();
        String label = "";

        for (ICommand commandData : this.commands) {
            for (String alias : commandData.getNames()) {
                if ((commandLine.length() == alias.length() && commandLine.toLowerCase().startsWith(alias.toLowerCase()))
                        || commandLine.toLowerCase().startsWith(alias.toLowerCase() + " ")) {
                    found = Optional.of(commandData);
                    label = alias;
                    if (commandLine.length() > alias.length() + 1) {
                        args = commandLine.substring(alias.length() + 1).split(" ");
                    }
                    break;
                }
            }
        }

        if (!found.isPresent()) {
            return new ArrayList<>();
        }

        ICommand command = found.get();

        if (!command.getSubCommands().isEmpty()) {
            Optional<ICommand> subCommandFound = Optional.empty();
            String[] subCommandArgs = new String[0];

            String subCommandLabel = "";

            for (ICommand commandData : command.getSubCommands()) {
                for (String alias : commandData.getNames()) {
                    if ((commandLine.length() == (label.length() + alias.length() + 1) && commandLine.toLowerCase().startsWith(label + " " + alias.toLowerCase()))
                            || commandLine.toLowerCase().startsWith(label + " " + alias.toLowerCase() + " ")) {
                        subCommandFound = Optional.of(commandData);
                        subCommandLabel = alias;
                        if (commandLine.length() > label.length() + alias.length() + 2) {
                            subCommandArgs = commandLine.substring(label.length() + 1).substring(alias.length() + 1).split(" ");
                        }
                        break;
                    }
                }
            }

            if (subCommandFound.isPresent()) {
                command = subCommandFound.get();

                args = subCommandArgs;

                label = label + " " + subCommandLabel;
            }

        }

        if (!command.getPermission().trim().isEmpty() && !authorizer.isAuthorized(namespace, command.getPermission())) {
            throw new NoPermissionsException(command.getPermissionMessage());
        }

        ArgumentArray arguments = new ArgumentArray(args);
        namespace.setObject(String.class, "label", label);

        return command.getSuggestions(namespace, arguments);
    }

    @Override
    public void registerCommand(ICommand command) {
        logger.log(Level.INFO, "Registered command {0}", command.getNames()[0]);

        this.commands.add(command);
    }

    @Override
    public void unregisterCommand(ICommand command) {
        while (commands.contains(command)) {
            commands.remove(command);
        }

        logger.log(Level.INFO, "Unregistered command {0}", command.getNames()[0]);
    }

    @Override
    public void unregisterAllCommands() {
        new ArrayList<>(commands).forEach(this::unregisterCommand);
    }

    @Override
    public Optional<ICommand> getCommand(String commandName) {
        Optional<ICommand> found = Optional.empty();

        for (ICommand commandData : this.commands) {
            for (String alias : commandData.getNames()) {
                if ((commandName.length() == alias.length() && commandName.toLowerCase().startsWith(alias.toLowerCase()))
                        || commandName.toLowerCase().startsWith(alias.toLowerCase() + " ")) {
                    found = Optional.of(commandData);
                    break;
                }
            }
        }

        return found;
    }

    @Override
    public boolean isCommandRegistered(String commandName) {
        for (ICommand commandData : this.commands) {
            for (String alias : commandData.getNames()) {
                if ((commandName.length() == alias.length() && commandName.toLowerCase().startsWith(alias.toLowerCase()))
                        || commandName.toLowerCase().startsWith(alias.toLowerCase() + " ")) {
                    return true;
                }
            }
        }

        return false;
    }
}
