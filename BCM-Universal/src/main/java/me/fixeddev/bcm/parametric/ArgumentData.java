package me.fixeddev.bcm.parametric;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArgumentData implements ParameterData {

    private String name;
    private List<Annotation> modifiers;
    private Class<?> parameterType;
    private String defaultValue;

    public ArgumentData(String name, List<Annotation> modifiers, Class<?> parameterType, String defaultValue) {
        this.name = name;
        this.modifiers = modifiers;
        this.parameterType = parameterType;
        this.defaultValue = defaultValue;
    }

    public ArgumentData(String name, Class<?> parameterType, String defaultValue) {
        this(name, new ArrayList<>(), parameterType, defaultValue);
    }

    /**
     * @return - The name of this parameter
     */
    public String getName() {
        return name;
    }

    public List<Annotation> getModifiers() {
        return modifiers;
    }

    @Override
    public Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.ARGUMENT;
    }

    /**
     * @return - The default value of this parameter, absent if this parameter doesn't has a default value
     * "false" if the ParameterType is FLAG
     */
    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }
}
