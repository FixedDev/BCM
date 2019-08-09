package me.fixeddev.bcm.parametric.providers.defaults;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.providers.ParameterProvider;
import me.fixeddev.bcm.parametric.annotation.JoinedString;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;

public class JoinedStringProvider implements ParameterProvider<String> {
    @Override
    public String transformParameter(ArgumentStack arguments, Namespace namespace, Annotation annotation) throws NoMoreArgumentsException, ArgumentsParseException {
        if (!(annotation instanceof JoinedString)) {
            throw new ArgumentsParseException(null, String.class, annotation.annotationType(), "The annotation type isn't JoinedString");
        }

        JoinedString joinedString = (JoinedString) annotation;

        StringJoiner joiner = new StringJoiner(" ");

        int consumedArgs = 1;

        while (arguments.hasNext()
                && (joinedString.value() == -1 || consumedArgs <= joinedString.value())) {
            joiner.add(arguments.next());

            consumedArgs++;
        }

        String returnValue = joiner.toString();

        return returnValue.trim();

    }

    @Override
    public boolean isProvided() {
        return true;
    }
}
