package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.basic.Authorizer;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.PermissionMessageProvider;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.annotation.Flag;
import me.fixeddev.bcm.parametric.annotation.JoinedString;
import me.fixeddev.bcm.parametric.providers.BooleanProvider;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.AdvancedCommand;
import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.BasicCommandHandler;
import me.fixeddev.bcm.basic.ICommand;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import me.fixeddev.bcm.parametric.providers.DoubleProvider;
import me.fixeddev.bcm.parametric.providers.IntegerProvider;
import me.fixeddev.bcm.parametric.providers.JoinedStringProvider;
import me.fixeddev.bcm.parametric.providers.NamespaceProvider;
import me.fixeddev.bcm.parametric.providers.StringParameterProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParametricCommandHandler extends BasicCommandHandler implements ParametricCommandRegistry {

    private Map<Class<?>, Map<Class<?>, ParameterProvider>> parameterTransformers;

    public ParametricCommandHandler(Authorizer authorizer, PermissionMessageProvider messageProvider, Logger logger) {
        super(authorizer, messageProvider, logger);

        parameterTransformers = new ConcurrentHashMap<>();

        registerParameterTransfomer(Namespace.class, new NamespaceProvider());
        registerParameterTransfomer(String.class, new StringParameterProvider());
        registerParameterTransformer(String.class, JoinedString.class, new JoinedStringProvider());

        registerParameterTransfomer(boolean.class, new BooleanProvider());
        registerParameterTransfomer(Boolean.class, new BooleanProvider());

        registerParameterTransfomer(double.class, new DoubleProvider());
        registerParameterTransfomer(Double.class, new DoubleProvider());

        registerParameterTransfomer(int.class, new IntegerProvider());
        registerParameterTransfomer(Integer.class, new IntegerProvider());

    }

    @Override
    public <T> void registerParameterTransformer(@NotNull Class<T> clazz, Class<?> annotation, @NotNull ParameterProvider<T> parameterProvider) {
        if (hasRegisteredTransformer(clazz, annotation)) {
            if (annotation == null) {
                throw new IllegalStateException("Failed to register parameter transformer for class " + clazz.getName() + ", there's already a registered parameter transformer!");
            }
            throw new IllegalStateException("Failed to register parameter transformer for class " + clazz.getName() + " and annotation " + annotation.getName() + ", there's already a registered parameter transformer!");
        }
        parameterTransformers.computeIfAbsent(clazz, aClass -> new HashMap<>()).put(annotation, parameterProvider);
    }

    @Override
    public <T> boolean hasRegisteredTransformer(@NotNull Class<T> clazz, Class<?> annotationType) {
        return parameterTransformers.computeIfAbsent(clazz, aClass -> new HashMap<>()).containsKey(annotationType);
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

                Optional<AdvancedCommand> commandResult = treeResult.commandResult();

                if(commandResult.isPresent()){
                    boolean commandPresent = treeResult.commandResult().isPresent();
                    boolean commandRegistered = isCommandRegistered(commandResult.get().getNames()[0]);

                    if (commandPresent && !commandRegistered) {
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
    public Map<Class<?>, Map<Class<?>, ParameterProvider>> getRegisteredParameterTransformers() {
        return parameterTransformers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParameterProvider<T> getParameterTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType) {
        return (ParameterProvider<T>) parameterTransformers.computeIfAbsent(clazz, aClass -> new HashMap<>()).get(annotationType);
    }

    private CommandTreeResult createCommandTree(AdvancedCommand callable, String alias) {
        // Divide the alias in parts divided by a space
        String[] aliasParts = alias.split(" ");

        // Check if the alias has more than 1 part(check if has spaces)
        if (aliasParts.length > 1) {
            // Get the Optional of a command with the name of the part 1 of the alias
            Optional<ICommand> command = getCommand(aliasParts[0]);

            // Check if the command exists and then if the type is not AdvancedCommand then stop all this process, because we can't register commands on an ICommand
            if (command.isPresent() && !(command.get() instanceof AdvancedCommand)) {
                logger.log(Level.SEVERE, "The parent command {0} can''t be registered because a command with the same name and different type is already registered!", aliasParts[0]);
                // Stop the process in a good way
                return createTreeResult(null, false);
            }

            // Create the main/root command which is a command already created with the name of the part 1 of the alias, or a new command
            AdvancedCommand mainCommand = (AdvancedCommand) command.orElse(getParentCommand(aliasParts, callable));
            AdvancedCommand parentCommand = mainCommand;

            // Iterate all the parts of the alias to create a command tree
            for (int i = 0; i < aliasParts.length; i++) {
                String aliasPart = aliasParts[i];

                if (i == 0) {
                    continue;
                }

                // This is a little bit confusing, but this checks if the part in which we're is the last of them
                if (i == (aliasParts.length - 1)) {
                    AdvancedCommand subCommand = getCommandWrapper(aliasPart, callable);

                    parentCommand.registerSubCommand(subCommand);

                    continue;
                }

                AdvancedCommand subCommand = getSubCommand(aliasPart, callable);

                parentCommand.registerSubCommand(subCommand);

                parentCommand = subCommand;
            }

            return createTreeResult(mainCommand, true);
        }


        return createTreeResult(null, false);
    }

    private CommandTreeResult createTreeResult(@Nullable AdvancedCommand command, boolean registeredSubCommands) {
        return new CommandTreeResult() {
            @Override
            public Optional<AdvancedCommand> commandResult() {
                return Optional.ofNullable(command);
            }

            @Override
            public boolean subCommandsRegistered() {
                return registeredSubCommands;
            }
        };
    }

    private AdvancedCommand getParentCommand(String[] aliasParts, AdvancedCommand callable) {
        return new AbstractAdvancedCommand(
                new String[]{aliasParts[0]},
                "/<command> <subcommand>",
                callable.getDescription(),
                callable.getPermission(),
                callable.getPermissionMessage(),
                new ArrayList<>(),
                0,
                -1,
                callable.allowAnyFlags(),
                callable.getExpectedFlags()) {

            @Override
            public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) throws NoMoreArgumentsException {
                return getSubCommandSuggestions(arguments, this);
            }

            @Override
            public boolean execute(CommandContext context) {
                return false;
            }
        };
    }

    private AdvancedCommand getSubCommand(String name, AdvancedCommand callable) {
        return new AbstractAdvancedCommand(
                new String[]{name},
                callable.getUsage(),
                callable.getDescription(),
                callable.getPermission(),
                callable.getPermissionMessage(),
                new ArrayList<>(),
                0,
                -1,
                callable.allowAnyFlags(),
                callable.getExpectedFlags()) {
            @Override
            public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) throws NoMoreArgumentsException {
                return getSubCommandSuggestions(arguments, this);
            }

            @Override
            public boolean execute(CommandContext context) {
                return false;
            }
        };
    }

    private List<String> getSubCommandSuggestions(ArgumentArray arguments, AdvancedCommand command) throws NoMoreArgumentsException {
        if (arguments.getPosition() > 0) {
            return new ArrayList<>();
        }

        String actualArgument = "";

        if (arguments.hasNext()) {
            actualArgument = arguments.next();
        }

        List<String> subCommands = command.getSubCommands().stream().map(cmd -> cmd.getNames()[0]).collect(Collectors.toList());

        List<String> suggestions = new ArrayList<>();

        for (String subCommand : subCommands) {
            if (!subCommand.startsWith(actualArgument)) {
                continue;
            }

            suggestions.add(subCommand);
        }

        return suggestions;
    }

    private AdvancedCommand getCommandWrapper(String name, AdvancedCommand callable) {
        return new AbstractAdvancedCommand(
                new String[]{name},
                callable.getUsage(),
                callable.getDescription(),
                callable.getPermission(),
                callable.getPermissionMessage(),
                new ArrayList<>(),
                callable.getMinArguments(),
                callable.getMaxArguments(),
                callable.allowAnyFlags(),
                callable.getExpectedFlags()) {
            @Override
            public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) throws CommandException, NoMoreArgumentsException {
                return callable.getSuggestions(namespace, arguments);
            }

            @Override
            public boolean execute(CommandContext context) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
                return callable.execute(context);
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
                return null;
            }

            parametersData.add(parameterData);
        }

        return new ParametricCommandExecutor(commandClass, command, parametersData, this, clazzMethod);
    }

    protected ParameterData getParameterData(Method clazzMethod, Class<?> type, Annotation[] annotations) {
        List<Annotation> modifiers = new ArrayList<>();

        if (type == CommandContext.class) {
            return new ParameterData(type, new Parameter() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Parameter.class;
                }

                @Override
                public String value() {
                    return "context";
                }

                @Override
                public boolean isFlag() {
                    return false;
                }
            }, emptyOptionalAnnotation(), modifiers);
        }

        Parameter param = null;

        me.fixeddev.bcm.parametric.annotation.Optional optional = emptyOptionalAnnotation();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Flag) {
                param = FlagData.getFromFlag((Flag) annotation);

                continue;
            }

            if (annotation instanceof Parameter) {
                param = (Parameter) annotation;

                continue;
            }

            if (annotation instanceof me.fixeddev.bcm.parametric.annotation.Optional) {
                optional = (me.fixeddev.bcm.parametric.annotation.Optional) annotation;

                continue;
            }

            modifiers.add(annotation);
        }

        if (param == null) {
            return new ParameterData(type, new Parameter() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Parameter.class;
                }

                @Override
                public String value() {
                    return type.getSimpleName();
                }

                @Override
                public boolean isFlag() {
                    return false;
                }
            }, optional, modifiers);
        }

        if (param.isFlag() && (type != Boolean.class && type != boolean.class)) {
            logger.log(Level.WARNING, "The method {0} of class {1} has a flag parameter but the type isn''t boolean.", new Object[]{clazzMethod.getDeclaringClass().getName(), clazzMethod.getName()});

            return null;
        }

        if (param.isFlag()) {
            return new FlagData(param, optional, modifiers);
        }

        return new ParameterData(type, param, optional, modifiers);
    }

    private me.fixeddev.bcm.parametric.annotation.Optional emptyOptionalAnnotation() {
        return new me.fixeddev.bcm.parametric.annotation.Optional() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return me.fixeddev.bcm.parametric.annotation.Optional.class;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }

}
