package me.fixeddev.bcm.parametric.providers;

import me.fixeddev.bcm.parametric.ParameterProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ParameterProviderRegistry {
    @NotNull
    Map<Class<?>, Map<Class<?>, ParameterProvider>> getRegisteredParameterTransformers();

    @Nullable
    <T> ParameterProvider<T> getParameterTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType);

    <T> void registerParameterTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType, @NotNull ParameterProvider<T> parameterProvider);

    <T> boolean hasRegisteredTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType);

    default void installModule(ProvidersModule module){
        module.configure(this);
    }

    default <T> ParameterProvider<T> getParameterTransformer(@NotNull Class<T> clazz) {
        return getParameterTransformer(clazz, null);
    }

    default <T> void registerParameterTransfomer(@NotNull Class<T> clazz, @NotNull ParameterProvider<T> parameterProvider) {
        registerParameterTransformer(clazz, null, parameterProvider);
    }

    default <T> boolean hasRegisteredTransformer(@NotNull Class<T> clazz) {
        return hasRegisteredTransformer(clazz, null);
    }

    static ParameterProviderRegistry createRegistry() {
        return new ParameterProviderRegistryImpl();
    }
}
