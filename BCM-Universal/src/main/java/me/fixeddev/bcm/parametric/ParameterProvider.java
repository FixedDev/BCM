package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public interface ParameterProvider<T> {
    T transformParameter(ArgumentStack arguments, Namespace namespace, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException;

    default List<String> getSuggestions(String text, Namespace namespace){
        return new ArrayList<>();
    }

    default boolean isProvided(){
        return false;
    }
}
