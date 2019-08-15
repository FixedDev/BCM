package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;

import java.lang.annotation.Annotation;

public class StringParameterProvider implements ParameterProvider<String> {
    @Override
    public String transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation) throws NoMoreArgumentsException {
        return arguments.next();
    }

    @Override
    public boolean isProvided() {
        return true;
    }
}
