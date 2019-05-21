package me.fixeddev.bcm.parametric.providers;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.ParameterProvider;
import me.fixeddev.bcm.parametric.annotation.JoinedString;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class StringParameterProvider implements ParameterProvider<String> {
    @Override
    public String transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException {
        Optional<JoinedString> found = Optional.empty();

        for (Annotation modifier : modifiers) {
            if (modifier == null || modifier.annotationType() != JoinedString.class) {
                continue;
            }

            found = Optional.of((JoinedString) modifier);
        }

        try {
            if (found.isPresent()) {
                StringJoiner joiner = new StringJoiner(" ");

                int consumedArgs = 1;

                while (arguments.hasNext()
                        && (found.get().value() == -1 || consumedArgs <= found.get().value())) {
                    joiner.add(arguments.next());

                    consumedArgs++;
                }

                String returnValue = joiner.toString();

                if ((defaultValue != null && !defaultValue.trim().isEmpty())
                        && returnValue.trim().isEmpty()) {
                    return defaultValue;
                }

                return returnValue.trim();
            }


            return arguments.next();
        } catch (NoMoreArgumentsException e) {
            if (defaultValue == null || defaultValue.isEmpty()) {
                throw e;
            }

            return defaultValue;
        }
    }

    @Override
    public boolean isProvided() {
        return true;
    }
}
