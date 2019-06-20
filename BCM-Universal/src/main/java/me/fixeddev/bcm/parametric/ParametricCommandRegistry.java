package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.basic.CommandRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ParametricCommandRegistry extends CommandRegistry {
    void registerCommandClass(CommandClass commandClass);

    @NotNull
    Map<Class<?>, Map<Class<?>, ParameterProvider>> getRegisteredParameterTransformers();

    @Nullable
    <T> ParameterProvider<T> getParameterTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType);

    <T> void registerParameterTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType, @NotNull ParameterProvider<T> parameterProvider);

    <T> boolean hasRegisteredTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType);

    default <T> ParameterProvider<T> getParameterTransformer(@NotNull Class<T> clazz) {
        return getParameterTransformer(clazz, null);
    }

    default <T> void registerParameterTransfomer(@NotNull Class<T> clazz, @NotNull ParameterProvider<T> parameterProvider) {
        registerParameterTransformer(clazz, null, parameterProvider);
    }

    default <T> boolean hasRegisteredTransformer(@NotNull Class<T> clazz) {
        return hasRegisteredTransformer(clazz, null);
    }
}
