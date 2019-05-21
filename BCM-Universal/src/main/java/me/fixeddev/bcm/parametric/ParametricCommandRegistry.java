package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.basic.CommandRegistry;

import java.util.Map;

public interface ParametricCommandRegistry extends CommandRegistry {
    void registerCommandClass(CommandClass commandClass);

    Map<Class<?>, ParameterProvider> getRegisteredParameterTransformers();

    <T> ParameterProvider<T> getParameterTransformer(Class<T> clazz);

    <T> void registerParameterTransfomer(Class<T> clazz, ParameterProvider<T> parameterProvider);

    <T> boolean hasRegisteredTransformer(Class<T> clazz);
}
