package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;

import java.lang.annotation.Annotation;

public class NamespaceProvider implements ParameterProvider<Namespace> {
    @Override
    public Namespace transformParameter(ArgumentStack arguments, Namespace namespace,Annotation annotation, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        return namespace;
    }


}
