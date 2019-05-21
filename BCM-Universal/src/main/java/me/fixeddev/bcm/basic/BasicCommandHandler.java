package me.fixeddev.bcm.basic;

import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandUsageException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicCommandHandler implements CommandRegistry, CommandDispatcher {

    private final Authorizer authorizer;
    private final PermissionMessageProvider messageProvider;

    private Map<String, ICommand> commandMap;

    protected Logger logger;

    public BasicCommandHandler(Authorizer authorizer, PermissionMessageProvider messageProvider, Logger logger) {
        this.authorizer = authorizer;
        this.messageProvider = messageProvider == null ? new NoOpPermissionMessageProvider() : messageProvider;

        commandMap = new ConcurrentHashMap<>();

        this.logger = logger;
    }

    @Override
    public Authorizer getAuthorizer() {
        return authorizer;
    }

    @Override
    public PermissionMessageProvider getPermissionMessageProvider() {
        return messageProvider;
    }

    @Override
    public CommandRegistry getRegistry() {
        return this;
    }

    @Override
    public boolean dispatchCommand(Namespace namespace, String commandLine) throws CommandException, NoPermissionsException, CommandUsageException, ArgumentsParseException {
        Optional<CommandSearchResult> result = getCommandFromCommandLine(commandLine);

        if (!result.isPresent()) {
            return false;
        }

        CommandSearchResult searchResult = result.get();

        String[] args = searchResult.getNewArguments();
        String label = searchResult.getLabel();
        ICommand command = searchResult.getCommand();

        if (!command.getPermission().trim().isEmpty() && !authorizer.isAuthorized(namespace, command.getPermission())) {
            if (!command.isPermissionMessageOverride()) {
                throw new NoPermissionsException(command.getPermissionMessage());
            }

            throw new NoPermissionsException(getPermissionMessageProvider().getMessage(command, namespace));
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
        Optional<CommandSearchResult> result = getCommandFromCommandLine(commandLine);

        if (!result.isPresent()) {
            return new ArrayList<>();
        }

        CommandSearchResult searchResult = result.get();

        String[] args = searchResult.getNewArguments();
        String label = searchResult.getLabel();
        ICommand command = searchResult.getCommand();

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

        for (String name : command.getNames()) {
            this.commandMap.put(name, command);
        }
    }

    @Override
    public void unregisterCommand(ICommand commandToRemove) {
        for (String name : commandToRemove.getNames()) {
            ICommand command = commandMap.get(name);

            if (command == commandToRemove) {
                commandMap.remove(name);
            }
        }

        logger.log(Level.INFO, "Unregistered command {0}", commandToRemove.getNames()[0]);
    }

    @Override
    public void unregisterAllCommands() {
        commandMap.values().forEach(this::unregisterCommand);
    }

    @Override
    public Optional<ICommand> getCommand(String commandName) {
        return Optional.ofNullable(commandMap.get(commandName));
    }

    @Override
    public Optional<CommandSearchResult> getCommandFromCommandLine(String commandLine) {
        String[] args = commandLine.split(" ");
        String label = "";

        int i = 0;
        while (!commandMap.containsKey(label) && i < args.length) {
            label = args[i];
            i++;
        }

        ICommand command = commandMap.get(label);

        if (command == null) {
            return Optional.empty();
        }

        args = Arrays.copyOfRange(args, i, args.length);

        while (!command.getSubCommands().isEmpty() && args.length >= 1) {
            ICommand subCommandFound;
            String[] subCommandArgs;

            String subCommandLabel = "";

            Map<String, ICommand> subCommands = new HashMap<>();

            for (ICommand subCommand : command.getSubCommands()) {
                for (String name : subCommand.getNames()) {
                    subCommands.put(name, subCommand);
                }
            }

            i = 0;

            while (!subCommands.containsKey(subCommandLabel) && i < args.length) {
                subCommandLabel = args[i];
                i++;
            }

            subCommandFound = subCommands.get(subCommandLabel);

            subCommandArgs = Arrays.copyOfRange(args, i, args.length);

            if (subCommandFound != null) {
                command = subCommandFound;

                args = subCommandArgs;

                label = label + " " + subCommandLabel;
            }
        }

        return Optional.of(fromValues(command, args, label));
    }

    @Override
    public boolean isCommandRegistered(String commandName) {
        return commandMap.containsKey(commandName);
    }

    private CommandSearchResult fromValues(ICommand command, String[] args, String label) {
        return new CommandSearchResult() {
            @Override
            public ICommand getCommand() {
                return command;
            }

            @Override
            public String[] getNewArguments() {
                return args;
            }

            @Override
            public String getLabel() {
                return label;
            }
        };
    }
}
