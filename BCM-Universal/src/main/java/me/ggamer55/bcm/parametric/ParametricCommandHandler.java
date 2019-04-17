package me.ggamer55.bcm.parametric;

import me.ggamer55.bcm.AbstractAdvancedCommand;
import me.ggamer55.bcm.AdvancedCommand;
import me.ggamer55.bcm.CommandContext;
import me.ggamer55.bcm.basic.*;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.CommandException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.basic.exceptions.NoPermissionsException;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.Parameter;
import me.ggamer55.bcm.parametric.providers.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParametricCommandHandler extends BasicCommandHandler implements ParametricCommandRegistry {

    private Map<Class<?>, ParameterProvider> parameterTransformerMap;

    public ParametricCommandHandler(Authorizer authorizer, PermissionMessageProvider messageProvider, Logger logger) {
        super(authorizer, messageProvider,logger);

        parameterTransformerMap = new ConcurrentHashMap<>();

        registerParameterTransfomer(Namespace.class, new NamespaceProvider());
        registerParameterTransfomer(String.class, new StringParameterProvider());

        registerParameterTransfomer(boolean.class, new BooleanProvider());
        registerParameterTransfomer(Boolean.class, new BooleanProvider());

        registerParameterTransfomer(double.class, new DoubleProvider());
        registerParameterTransfomer(Double.class, new DoubleProvider());

        registerParameterTransfomer(int.class, new IntegerProvider());
        registerParameterTransfomer(Integer.class, new IntegerProvider());

    }

    @Override
    public void registerCommandClass(CommandClass commandClass) {
        Class<?> clazz = commandClass.getClass();

        Method[] clazzMethods = clazz.getDeclaredMethods();

        if (clazzMethods.length == 0) {
            logger.log(Level.WARNING, "The class {0} doesn't have any methods.", clazz.getName());
            return;
        }

        for (Method clazzMethod : clazzMethods) {
            AdvancedCommand callable = getCommandCallable(clazzMethod, commandClass);

            if (callable == null) {
                continue;
            }

            boolean registeredSubCommands = false;

            for (String alias : callable.getNames()) {
                String[] aliasParts = alias.split(" ");

                if (aliasParts.length > 1) {
                    Optional<ICommand> command = getCommand(aliasParts[0]);

                    if (command.isPresent() && !(command.get() instanceof AdvancedCommand)) {
                        logger.log(Level.SEVERE, "The parent command {0} can't be registered because a command with the same name is already registered!", aliasParts[0]);
                        continue;
                    }

                    AdvancedCommand parentCommand = (AdvancedCommand) command.orElse(_getParentCommand(aliasParts, callable));

                    if (!isCommandRegistered(aliasParts[0])) {
                        registerCommand(parentCommand);
                    }

                    for (int i = 0; i < aliasParts.length; i++) {
                        String aliasPart = aliasParts[i];

                        if (i == 0) {
                            continue;
                        }

                        if (i == (aliasParts.length - 1)) {
                            AdvancedCommand subCommand = _getCommandWrapper(aliasPart, callable);

                            parentCommand.registerSubCommand(subCommand);

                            continue;
                        }

                        AdvancedCommand subCommand = _getSubCommand(aliasPart, callable);

                        parentCommand.registerSubCommand(subCommand);

                        parentCommand = subCommand;

                    }

                    registeredSubCommands = true;
                }
            }

            if (!registeredSubCommands) {
                registerCommand(callable);
            }
        }
    }

    private AdvancedCommand _getParentCommand(String[] aliasParts, AdvancedCommand callable) {
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
                if (arguments.getPosition() > 0) {
                    return new ArrayList<>();
                }

                String actualArgument = "";

                if (arguments.hasNext()) {
                    actualArgument = arguments.next();
                }

                List<String> subCommands = getSubCommands().stream().map(cmd -> cmd.getNames()[0]).collect(Collectors.toList());

                List<String> suggestions = new ArrayList<>();

                for (String subCommand : subCommands) {
                    if (!subCommand.startsWith(actualArgument)) {
                        continue;
                    }

                    suggestions.add(subCommand);
                }

                return suggestions;
            }

            @Override
            public boolean execute(CommandContext context) {
                return false;
            }
        };
    }

    private AdvancedCommand _getSubCommand(String name, AdvancedCommand callable) {
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
                String actualArgument = "";

                if (arguments.hasNext()) {
                    actualArgument = arguments.next();
                }

                List<String> subCommands = getSubCommands().stream().map(cmd -> cmd.getNames()[0]).collect(Collectors.toList());

                List<String> suggestions = new ArrayList<>();

                for (String subCommand : subCommands) {
                    if (!subCommand.startsWith(actualArgument)) {
                        continue;
                    }

                    suggestions.add(subCommand);
                }

                return suggestions;
            }

            @Override
            public boolean execute(CommandContext context) {
                return false;
            }
        };
    }

    private AdvancedCommand _getCommandWrapper(String name, AdvancedCommand callable) {
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
            logger.log(Level.WARNING, "The method {0} doesn't return boolean or Boolean", clazzMethod.getName());
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
        ParametricCommandExecutor callable = new ParametricCommandExecutor(commandClass, command, parametersData, this, clazzMethod);

        return callable;
    }

    protected ParameterData getParameterData(Method clazzMethod, Class<?> parameterType, Annotation[] annotations) {
        List<Annotation> modifiers = new ArrayList<>();

        Class<?> type = parameterType;

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
        me.ggamer55.bcm.parametric.annotation.Optional optional = emptyOptionalAnnotation();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Parameter) {
                param = (Parameter) annotation;
            }
            if(annotation instanceof me.ggamer55.bcm.parametric.annotation.Optional){
                optional = (me.ggamer55.bcm.parametric.annotation.Optional) annotation;
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
            logger.log(Level.WARNING, "The method {0} of class {1} has a flag parameter but the type isn't boolean.", new Object[]{clazzMethod.getDeclaringClass().getName(), clazzMethod.getName()});

            return null;
        }

        return new ParameterData(type, param, optional, modifiers);
    }

    private me.ggamer55.bcm.parametric.annotation.Optional emptyOptionalAnnotation() {
        return new me.ggamer55.bcm.parametric.annotation.Optional() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return me.ggamer55.bcm.parametric.annotation.Optional.class;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }

    @Override
    public Map<Class<?>, ParameterProvider> getRegisteredParameterTransformers() {
        return new HashMap<>(parameterTransformerMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParameterProvider<T> getParameterTransformer(Class<T> clazz) {
        return parameterTransformerMap.get(clazz);
    }

    @Override
    public <T> void registerParameterTransfomer(Class<T> clazz, ParameterProvider<T> parameterProvider) {
        if (hasRegisteredTransformer(clazz)) {
            throw new IllegalStateException("Failed to register parameter transformer for class " + clazz.getName() + ", there's already a registered parameter transformer!");
        }

        parameterTransformerMap.put(clazz, parameterProvider);
    }

    @Override
    public <T> boolean hasRegisteredTransformer(Class<T> clazz) {
        return parameterTransformerMap.containsKey(clazz);
    }
}
