package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;

import java.lang.annotation.Annotation;

public class IntegerProvider implements ParameterProvider<Integer> {
    @Override
    public Integer transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        try {
            return arguments.nextInt();
        } catch (ArgumentsParseException e) {
            if (defaultValue == null || defaultValue.isEmpty()) {
                throw e;
            }

            return Integer.parseInt(defaultValue);
        }
    }
    @Override
    public boolean isProvided() {
        return true;
    }
}
