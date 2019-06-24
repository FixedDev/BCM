package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.AdvancedCommand;
import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.ICommand;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.exceptions.NoTransformerFound;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

class ParametricCommandExecutor implements AdvancedCommand {

    private CommandClass instance;
    private Command command;
    private List<ParameterData> parameters;

    private ParametricCommandRegistry registry;

    private Method method;

    private List<ICommand> subCommands;

    private List<Character> flags;

    public ParametricCommandExecutor(CommandClass instance, Command command, List<ParameterData> parameters, ParametricCommandRegistry registry, Method method) {
        this.instance = instance;
        this.command = command;
        this.parameters = parameters;
        this.registry = registry;
        this.method = method;

        subCommands = new CopyOnWriteArrayList<>();
    }

    @Override
    public int getMinArguments() {
        return command.min();
    }

    @Override
    public int getMaxArguments() {
        return command.max();
    }

    @Override
    public boolean allowAnyFlags() {
        return command.anyFlags();
    }

    @Override
    public List<Character> getExpectedFlags() {
        if (flags == null) {
            flags = new ArrayList<>();

            for (char flag : command.flags()) {
                flags.add(flag);
            }
        }

        return new ArrayList<>(flags);
    }

    @Override
    public String[] getNames() {
        return command.names();
    }

    @Override
    public String getUsage() {
        return command.usage();
    }

    @Override
    public String getDescription() {
        return command.desc();
    }

    @Override
    public String getPermission() {
        return command.permission();
    }

    @Override
    public String getPermissionMessage() {
        return command.permissionMessage();
    }

    @Override
    public List<ICommand> getSubCommands() {
        return new ArrayList<>(subCommands);
    }

    public void registerSubCommand(ICommand command) {
        subCommands.add(command);
    }

    public void unregisterSubCommand(ICommand command) {
        while (subCommands.contains(command)) {
            subCommands.remove(command);
        }
    }

    @Override
    public boolean execute(CommandContext context) throws CommandException, ArgumentsParseException {
        List<Object> arguments = new ArrayList<>();

        ArgumentStack argumentStack = context.getRawArgumentsWithoutFlags();

        // TODO: Move parameters parsing out of there to another class
        for (ParameterData data : parameters) {
            String name = data.getName();
            Class<?> type = data.getType();

            Annotation firstAnnotation = null;
            Class<?> annotationType = null;

            if (!data.getModifiers().isEmpty()) {
                firstAnnotation = data.getModifiers().get(0);
                annotationType = firstAnnotation.annotationType();
            }

            boolean isFlag = data instanceof FlagData;

            if (isFlag) {
                arguments.add(context.getFlagValue(name.charAt(0)));

                continue;
            }

            if (type == CommandContext.class) {
                arguments.add(context);

                continue;
            }

            if (!registry.hasRegisteredTransformer(type)) {
                throw new CommandException(new NoTransformerFound(type));
            }

            ParameterProvider transformer = registry.getParameterTransformer(type, annotationType);

            if (transformer == null) {
                transformer = registry.getParameterTransformer(type);
            }

            Object object;

            try {
                object = transformer.transformParameter(argumentStack, context.getNamespace(), firstAnnotation, data.getDefaultValue());
            } catch (NoMoreArgumentsException e) {
                throw new CommandException(e);
            }

            arguments.add(object);
        }

        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        try {
            return (boolean) method.invoke(instance, arguments.toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CommandException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getSuggestions(Namespace namespace, ArgumentArray argumentArray) throws CommandException, NoMoreArgumentsException {
        List<String> suggestions = new ArrayList<>();

        Map<Integer, Class<?>> typeMap = new HashMap<>();
        Map<Integer, Class<?>> annotationTypeMap = new HashMap<>();

        int index = 0;

        for (ParameterData parameter : parameters) {
            boolean isFlag = parameter instanceof FlagData;

            if (isFlag || parameter.getType() == CommandContext.class) {
                continue;
            }

            Class<?> annotationType = null;

            if (!parameter.getModifiers().isEmpty()) {
                annotationType = parameter.getModifiers().get(0).annotationType();
            }

            if (!registry.hasRegisteredTransformer(parameter.getType(), annotationType)) {
                throw new CommandException(new NoTransformerFound(parameter.getType()));
            }

            ParameterProvider transformer = registry.getParameterTransformer(parameter.getType(), annotationType);

            if (!transformer.isProvided()) {
                continue;
            }

            typeMap.put(index, parameter.getType());
            annotationTypeMap.put(index, annotationType);

            index++;
        }

        int argumentIndex = argumentArray.getSize();

        if (argumentIndex >= typeMap.size()) {
            return suggestions;
        }

        Class<?> parameterType = typeMap.get(argumentIndex);
        Class<?> annotationType = annotationTypeMap.get(argumentIndex);

        if (!registry.hasRegisteredTransformer(parameterType, annotationType)) {
            throw new CommandException(new NoTransformerFound(parameterType));
        }

        ParameterProvider transformer = registry.getParameterTransformer(parameterType,annotationType);

        String parameterText = argumentArray.get(argumentIndex);

        parameterText = parameterText == null ? "" : parameterText;

        suggestions.addAll(transformer.getSuggestions(parameterText, namespace));

        return suggestions;
    }


}
