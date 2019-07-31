package me.fixeddev.bcm.examples.parametric;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.examples.Sender;
import me.fixeddev.bcm.parametric.ParameterProvider;

import java.lang.annotation.Annotation;

public class SenderProvider implements ParameterProvider<Sender> {
    @Override
    public Sender transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation, String defaultValue) {
        return namespace.getObject(Sender.class, "sender");
    }

    // The parameter is part of the parsed arguments, or it's retrieved in another way
    @Override
    public boolean isProvided() {
        return false;
    }
}
