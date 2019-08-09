package me.fixeddev.bcm.parametric;

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
