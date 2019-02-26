package me.ggamer55.bcm.parametric.providers;

import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.parametric.ParameterProvider;

import java.lang.annotation.Annotation;
import java.util.List;

public class NamespaceProvider implements ParameterProvider<Namespace> {
    @Override
    public Namespace transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        return namespace;
    }


}
