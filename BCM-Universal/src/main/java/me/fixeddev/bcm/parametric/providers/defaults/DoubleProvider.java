package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.ParameterProvider;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;

import java.lang.annotation.Annotation;
import java.util.List;

public class DoubleProvider implements ParameterProvider<Double> {
    @Override
    public Double transformParameter(ArgumentStack arguments, Namespace namespace,Annotation annotation, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        try {
            return arguments.nextDouble();
        } catch (ArgumentsParseException e) {
            if (defaultValue == null || defaultValue.isEmpty()) {
                throw e;
            }

            return Double.parseDouble(defaultValue);
        }
    }
    @Override
    public boolean isProvided() {
        return true;
    }
}
