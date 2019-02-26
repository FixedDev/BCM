package me.ggamer55.bcm.parametric.providers;

import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.parametric.ParameterProvider;

import java.lang.annotation.Annotation;
import java.util.List;

public class IntegerProvider implements ParameterProvider<Integer> {
    @Override
    public Integer transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
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
