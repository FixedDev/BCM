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
import me.fixeddev.bcm.parametric.exceptions.NoTransformerFoundException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ParametricCommandExecutor implements AdvancedCommand {

    private CommandClass instance;
    private Command command;
    private List<ParameterData> parameters;

    private ParameterProviderRegistry providerRegistry;

    private Method method;

    private List<ICommand> subCommands;

    private List<Character> flags;

    public ParametricCommandExecutor(CommandClass instance, Command command, List<ParameterData> parameters, ParameterProviderRegistry registry, Method method) {
        this.instance = instance;
        this.command = command;
        this.parameters = parameters;
        providerRegistry = registry;
        this.method = method;

        subCommands = new ArrayList<>();
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

        return flags;
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
        return subCommands;
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

        for (ParameterData data : parameters) {
            if (data.getType() == ParameterType.FLAG) {
                FlagData flagData = (FlagData) data;

                arguments.add(context.getFlagValue(flagData.getName()));

                continue;
            }

            ArgumentData argumentData = (ArgumentData) data;

            String name = argumentData.getName();
            Class<?> type = argumentData.getParameterType();

            Annotation firstAnnotation = null;
            Class<?> annotationType = null;

            if (!argumentData.getModifiers().isEmpty()) {
                firstAnnotation = argumentData.getModifiers().get(0);
                annotationType = firstAnnotation.annotationType();
            }

            if (type == CommandContext.class) {
                arguments.add(context);

                continue;
            }

            if (!providerRegistry.hasRegisteredTransformer(type, annotationType)) {
                throw new NoTransformerFoundException(type);
            }

            ParameterProvider transformer = providerRegistry.getParameterTransformer(type, annotationType);

            if (transformer == null) {
                transformer = providerRegistry.getParameterTransformer(type);
            }

            Optional<String> defaultValue = ((ArgumentData) data).getDefaultValue();

            ArgumentStack defaultStack = new ArgumentStack(defaultValue.orElse("").split(" "));

            Object object;

            try {
                object = transformer.transformParameter(argumentStack, context.getNamespace(), firstAnnotation);

                if (object == null) {
                    object = transformer.transformParameter(defaultStack, context.getNamespace(), firstAnnotation);
                }
            } catch (ArgumentsParseException e) {
                object = transformer.transformParameter(defaultStack, context.getNamespace(), firstAnnotation);
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
    public List<String> getSuggestions(Namespace namespace, ArgumentArray argumentArray) throws CommandException {
        List<String> suggestions = new ArrayList<>();

        Map<Integer, Class<?>> typeMap = new HashMap<>();
        Map<Integer, Class<?>> annotationTypeMap = new HashMap<>();

        int index = 0;

        for (ParameterData parameter : parameters) {
            if (parameter.getType() == ParameterType.FLAG || parameter.getParameterType() == CommandContext.class) {
                continue;
            }

            ArgumentData data = (ArgumentData) parameter;

            Class<?> annotationType = null;

            if (!data.getModifiers().isEmpty()) {
                annotationType = data.getModifiers().get(0).annotationType();
            }

            if (!providerRegistry.hasRegisteredTransformer(data.getParameterType(), annotationType)) {
                throw new NoTransformerFoundException(data.getParameterType());
            }

            ParameterProvider transformer = providerRegistry.getParameterTransformer(data.getParameterType(), annotationType);

            if (!transformer.isProvided()) {
                continue;
            }

            typeMap.put(index, data.getParameterType());
            annotationTypeMap.put(index, annotationType);

            index++;
        }

        int argumentIndex = argumentArray.getSize() > 0 ? argumentArray.getSize() - 1 : 0;

        if (argumentIndex >= typeMap.size()) {
            return suggestions;
        }

        Class<?> parameterType = typeMap.get(argumentIndex);
        Class<?> annotationType = annotationTypeMap.get(argumentIndex);

        if (!providerRegistry.hasRegisteredTransformer(parameterType, annotationType)) {
            throw new NoTransformerFoundException(parameterType);
        }

        ParameterProvider transformer = providerRegistry.getParameterTransformer(parameterType, annotationType);

        String parameterText = argumentArray.get(argumentIndex);

        parameterText = parameterText == null ? "" : parameterText;

        suggestions.addAll(transformer.getSuggestions(parameterText, namespace));

        return suggestions;
    }


}
