package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;

import java.lang.annotation.Annotation;

public class DoubleProvider implements ParameterProvider<Double> {
    @Override
    public Double transformParameter(ArgumentStack arguments, Namespace namespace,Annotation annotation) throws NoMoreArgumentsException, ArgumentsParseException {
            return arguments.nextDouble();

    }
    @Override
    public boolean isProvided() {
        return true;
    }
}
