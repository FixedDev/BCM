package me.ggamer55.bcm.parametric.providers;

import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.parametric.ParameterProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class BooleanProvider implements ParameterProvider<Boolean> {

    private List<String> suggestions = Arrays.asList("true", "false");

    @Override
    public Boolean transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        try {
            return arguments.nextBoolean();
        } catch (ArgumentsParseException e) {
            if (defaultValue == null || defaultValue.isEmpty()) {
                throw e;
            }

            return Boolean.parseBoolean(defaultValue);
        }
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
