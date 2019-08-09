package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.parametric.annotation.Parameter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ArgumentData implements IParameterData {

    private String name;
    private Class<?> parameterType;
    private String defaultValue;

    public ArgumentData(String name, Class<?> parameterType, String defaultValue) {
        this.name = name;
        this.parameterType = parameterType;
        this.defaultValue = defaultValue;
    }

    public static ArgumentData valueOf(@NotNull Class<?> parameterType, @NotNull Parameter parameter, me.fixeddev.bcm.parametric.annotation.Optional optional) {
        String defaultValue = optional == null ? null : optional.value();
        return new ArgumentData(parameter.value(), parameterType, defaultValue);
    }

    /**
     * @return - The name of this parameter
     */
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.ARGUMENT;
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }
}
