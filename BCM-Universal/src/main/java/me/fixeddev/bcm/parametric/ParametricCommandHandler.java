package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.basic.Authorizer;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.PermissionMessageProvider;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.annotation.Flag;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.AdvancedCommand;
import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.BasicCommandHandler;
import me.fixeddev.bcm.basic.ICommand;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParametricCommandHandler extends BasicCommandHandler implements ParametricCommandRegistry {

    private ParameterProviderRegistry registry;

    public ParametricCommandHandler(Authorizer authorizer, PermissionMessageProvider messageProvider, ParameterProviderRegistry registry, Logger logger) {
        super(authorizer, messageProvider, logger);

        this.registry = registry;
    }


    @Override
    public void registerCommand(ICommand command) {
        Optional<ICommand> optRegisteredCommand = getCommand(command.getNames()[0]);

        if (optRegisteredCommand.isPresent() && optRegisteredCommand.get() instanceof MockCommand) {
            ICommand registeredCommand = optRegisteredCommand.get();

            if (command instanceof AdvancedCommand) {
                registeredCommand.getSubCommands().forEach(((AdvancedCommand) command)::registerSubCommand);
            }
        }

        super.registerCommand(command);
    }

    @Override
    public void registerCommandClass(CommandClass commandClass) {
        Class<?> clazz = commandClass.getClass();

        Method[] clazzMethods = clazz.getDeclaredMethods();

        if (clazzMethods.length == 0) {
            logger.log(Level.WARNING, "The class {0} doesn''t have any methods.", clazz.getName());
            return;
        }

        for (Method clazzMethod : clazzMethods) {
            AdvancedCommand callable = getCommandCallable(clazzMethod, commandClass);

            if (callable == null) {
                continue;
            }

            boolean registeredSubCommands = false;

            for (String alias : callable.getNames()) {
                CommandTreeResult treeResult = createCommandTree(callable, alias);

                Optional<ICommand> commandResult = treeResult.commandResult();

                if (commandResult.isPresent()) {
                    boolean commandRegistered = isCommandRegistered(commandResult.get().getNames()[0]);

                    if (!commandRegistered) {
                        registerCommand(commandResult.get());
                    }
                }

                if (!registeredSubCommands) {
                    registeredSubCommands = treeResult.subCommandsRegistered();
                }
            }

            if (!registeredSubCommands) {
                registerCommand(callable);
            }
        }
    }

    @NotNull
    @Override
    public ParameterProviderRegistry getParameterProviderRegistry() {
        return registry;
    }

    private CommandTreeResult createCommandTree(AdvancedCommand callable, String alias) {
        // Divide the alias in parts divided by a space
        String[] aliasParts = alias.split(" ");

        String parentAlias = String.join(" ", Arrays.copyOfRange(aliasParts, 0, aliasParts.length - 1));

        // Check if the alias has more than 1 part(check if has spaces)
        if (aliasParts.length > 1) {
            // Get the Optional of a command with the name of the part 1 of the alias
            Optional<ICommand> command = getCommand(aliasParts[0]);

            Optional<CommandSearchResult> searchResult = getCommandFromCommandLine(parentAlias);
            if (searchResult.isPresent()) {
                CommandSearchResult objectResult = searchResult.get();

                ICommand resultCommand = objectResult.getCommand();

                if (resultCommand instanceof AdvancedCommand) {
                    CommandWrapper wrapper = new CommandWrapper(aliasParts[aliasParts.length - 1], callable);

                    ((AdvancedCommand) resultCommand).registerSubCommand(wrapper);

                    return createTreeResult(command.get(), true);
                }

            }

            // Check if the command exists and then if the type is not AdvancedCommand then stop all this process, because we can't register commands on an ICommand
            if (command.isPresent() && !(command.get() instanceof AdvancedCommand)) {
                logger.log(Level.SEVERE, "The parent command {0} can''t be registered because a command with the same name and different type is already registered!", aliasParts[0]);
                // Stop the process in a good way
                return createTreeResult(null, false);
            }

            // Create the main/root command which is a command already created with the name of the part 1 of the alias, or a new command
            AdvancedCommand mainCommand = (AdvancedCommand) command.orElse(new MockCommand(aliasParts[0], "/<command> <subcommand>", callable));
            AdvancedCommand parentCommand = mainCommand;

            // Iterate all the parts of the alias to create a command tree
            for (int i = 0; i < aliasParts.length; i++) {
                String aliasPart = aliasParts[i];

                if (i == 0) {
                    continue;
                }

                // This is a little bit confusing, but this checks if the part in which we're is the last of them
                if (i == (aliasParts.length - 1)) {
                    AdvancedCommand subCommand = new CommandWrapper(aliasPart, callable);

                    parentCommand.registerSubCommand(subCommand);

                    continue;
                }

                AdvancedCommand subCommand = new MockCommand(aliasPart, callable.getUsage(), callable);

                parentCommand.registerSubCommand(subCommand);

                parentCommand = subCommand;
            }

            return createTreeResult(mainCommand, true);
        }

        return createTreeResult(null, false);
    }

    private CommandTreeResult createTreeResult(@Nullable ICommand command, boolean registeredSubCommands) {
        return new CommandTreeResult() {
            @Override
            public Optional<ICommand> commandResult() {
                return Optional.ofNullable(command);
            }

            @Override
            public boolean subCommandsRegistered() {
                return registeredSubCommands;
            }
        };
    }

    protected AdvancedCommand getCommandCallable(Method clazzMethod, CommandClass commandClass) {
        if (Modifier.isStatic(clazzMethod.getModifiers())) {
            return null;
        }

        if (!Modifier.isPublic(clazzMethod.getModifiers())) {
            return null;
        }

        if (clazzMethod.getReturnType() != boolean.class
                && clazzMethod.getReturnType() != Boolean.class) {
            logger.log(Level.WARNING, "The method {0} doesn''t return boolean or Boolean", clazzMethod.getName());
            return null;
        }

        if (!clazzMethod.isAnnotationPresent(Command.class)) {
            return null;
        }

        Command command = clazzMethod.getAnnotation(Command.class);

        List<ParameterData> parametersData = new ArrayList<>();

        for (int i = 0; i < clazzMethod.getParameterTypes().length; i++) {
            Class<?> type = clazzMethod.getParameterTypes()[i];
            Annotation[] annotations = clazzMethod.getParameterAnnotations()[i];

            ParameterData parameterData = getParameterData(clazzMethod, type, annotations);

            if (parameterData == null) {
                logger.log(Level.WARNING, "The parameter {0} of the method {1} doesn''t has a valid parameter data ", new Object[]{i, clazzMethod.getName()});
                return null;
            }

            parametersData.add(parameterData);
        }

        return new ParametricCommandExecutor(commandClass, command, parametersData, registry, clazzMethod);
    }

    protected ParameterData getParameterData(Method clazzMethod, Class<?> type, Annotation[] annotations) {
        List<Annotation> modifiers = new ArrayList<>();

        if (type == CommandContext.class) {
            return new ArgumentData("context", CommandContext.class, null);
        }

        Annotation dataAnnotation = null;

        String defaultValue = null;

        for (Annotation annotation : annotations) {
            if (annotation instanceof Flag || annotation instanceof Parameter) {
                if (dataAnnotation != null) {
                    throw new IllegalStateException("One of the parameters of the method " + clazzMethod.getName() + " has a Flag and a Parameter annotations or repeated annotations of these types!");
                }

                dataAnnotation = annotation;
                continue;
            }

            if (annotation instanceof me.fixeddev.bcm.parametric.annotation.Optional) {
                defaultValue = ((me.fixeddev.bcm.parametric.annotation.Optional) annotation).value();

                continue;
            }

            modifiers.add(annotation);
        }

        if (dataAnnotation == null) {
            return new ArgumentData(type.getSimpleName(), modifiers, type, defaultValue);
        }

        ParameterData parameterData;

        if (dataAnnotation instanceof Flag) {
            parameterData = new FlagData(((Flag) dataAnnotation).value());
        } else {
            parameterData = new ArgumentData(((Parameter) dataAnnotation).value(), modifiers, type, defaultValue);
        }

        if (parameterData.getType() == ParameterType.FLAG && (type != Boolean.class && type != boolean.class)) {
            logger.log(Level.WARNING, "The method {0} of class {1} has a flag parameter but the type isn''t boolean.", new Object[]{clazzMethod.getDeclaringClass().getName(), clazzMethod.getName()});

            return null;
        }

        return parameterData;
    }

    class CommandWrapper extends AbstractAdvancedCommand {

        private AdvancedCommand callable;

        public CommandWrapper(String name, AdvancedCommand callable) {
            super(new String[]{name},
                    callable.getUsage(),
                    callable.getDescription(),
                    callable.getPermission(),
                    callable.getPermissionMessage(),
                    new ArrayList<>(),
                    callable.getMinArguments(),
                    callable.getMaxArguments(),
                    callable.allowAnyFlags(),
                    callable.getExpectedFlags());

            this.callable = callable;
        }

        @Override
        public boolean execute(CommandContext context) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
            return callable.execute(context);
        }

        @Override
        public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) throws CommandException, NoMoreArgumentsException {
            return callable.getSuggestions(namespace, arguments);
        }
    }

    class MockCommand extends AbstractAdvancedCommand {
        public MockCommand(String name, String usage, AdvancedCommand callable) {
            super(new String[]{name},
                    usage,
                    callable.getDescription(),
                    callable.getPermission(),
                    callable.getPermissionMessage(),
                    new ArrayList<>(),
                    0,
                    -1,
                    callable.allowAnyFlags(),
                    callable.getExpectedFlags());
        }

        @Override
        public boolean execute(CommandContext context) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
            return false;
        }
    }
}
