package me.ggamer55.bcm.parametric;

import me.ggamer55.bcm.parametric.annotation.Parameter;

import java.lang.annotation.Annotation;
import java.util.List;

class ParameterData {

    private String name;

    private Class<?> type;
    private List<Annotation> modifiers;

    private boolean flag;

    private String defaultValue;

    public ParameterData(Class<?> type, Parameter parameter, List<Annotation> modifiers) {
        name = parameter.value();
        this.type = type;
        this.modifiers = modifiers;

        flag = parameter.isFlag();
        defaultValue = parameter.defaultValue();
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public List<Annotation> getModifiers() {
        return modifiers;
    }

    public boolean isFlag() {
        return flag;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
