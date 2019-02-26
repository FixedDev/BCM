package me.ggamer55.bcm.parametric;

import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public interface ParameterProvider<T> {
    T transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException;

    default List<String> getSuggestions(String text, Namespace namespace){
        return new ArrayList<>();
    }

    default boolean isProvided(){
        return false;
    }
}
