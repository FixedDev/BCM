package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class BooleanProvider implements ParameterProvider<Boolean> {

    private List<String> suggestions = Arrays.asList("true", "false");

    @Override
    public Boolean transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation) throws NoMoreArgumentsException, ArgumentsParseException {
        return arguments.nextBoolean();
    }

    @Override
    public List<String> getSuggestions(String text, Namespace namespace) {
        return suggestions;
    }

    @Override
    public boolean isProvided() {
        return true;
    }
}
