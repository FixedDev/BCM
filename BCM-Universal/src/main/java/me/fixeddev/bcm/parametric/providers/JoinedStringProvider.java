package me.fixeddev.bcm.parametric.providers;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.ParameterProvider;
import me.fixeddev.bcm.parametric.annotation.JoinedString;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;

public class JoinedStringProvider implements ParameterProvider<String> {
    @Override
    public String transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        if (!(annotation instanceof JoinedString)) {
            throw new ArgumentsParseException(null, String.class, annotation.annotationType(), "The annotation type isn't JoinedString");
        }

        JoinedString joinedString = (JoinedString) annotation;
        try {
            StringJoiner joiner = new StringJoiner(" ");

            int consumedArgs = 1;

            while (arguments.hasNext()
                    && (joinedString.value() == -1 || consumedArgs <= joinedString.value())) {
                joiner.add(arguments.next());

                consumedArgs++;
            }

            String returnValue = joiner.toString();

            if ((defaultValue != null && !defaultValue.trim().isEmpty())
                    && returnValue.trim().isEmpty()) {
                return defaultValue;
            }

            return returnValue.trim();
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
