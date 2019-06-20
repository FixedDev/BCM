package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.basic.CommandRegistry;

import java.util.Map;

public interface ParametricCommandRegistry extends CommandRegistry {
    void registerCommandClass(CommandClass commandClass);

    Map<Class<?>, ParameterProvider> getRegisteredParameterTransformers();

    <T> ParameterProvider<T> getParameterTransformer(Class<T> clazz);

    default <T> void registerParameterTransfomer(Class<T> clazz, ParameterProvider<T> parameterProvider) {
        registerParameterTransformer(clazz, null, parameterProvider);
    }

    <T> void registerParameterTransformer(Class<T> clazz, Class<?> annotationType, ParameterProvider<T> parameterProvider);

    default <T> boolean hasRegisteredTransformer(Class<T> clazz) {
        return hasRegisteredTransformer(clazz, null);
    }

    <T> boolean hasRegisteredTransformer(Class<T> clazz, Class<?> annotationType);
}
