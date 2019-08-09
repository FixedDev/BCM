package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;

import java.lang.annotation.Annotation;

public class StringParameterProvider implements ParameterProvider<String> {
    @Override
    public String transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation, String defaultValue) throws NoMoreArgumentsException {
        try {
            return arguments.next();
        } catch (NoMoreArgumentsException e) {
            if (defaultValue == null || defaultValue.isEmpty()) {
                throw e;
            }

            return defaultValue;
        }
    }

    @Override
    public boolean isProvided() {
        return true;
    }
}
