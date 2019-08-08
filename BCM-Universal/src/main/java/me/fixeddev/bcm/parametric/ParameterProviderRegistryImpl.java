package me.fixeddev.bcm.parametric;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ParameterProviderRegistryImpl implements ParameterProviderRegistry {
    private Map<Class<?>, Map<Class<?>, ParameterProvider>> parameterTransformers;

    @Override
    public <T> void registerParameterTransformer(@NotNull Class<T> clazz, Class<?> annotation, @NotNull ParameterProvider<T> parameterProvider) {
        if (hasRegisteredTransformer(clazz, annotation)) {
            if (annotation == null) {
                throw new IllegalStateException("Failed to register parameter transformer for class " + clazz.getName() + ", there's already a registered parameter transformer!");
            }
            throw new IllegalStateException("Failed to register parameter transformer for class " + clazz.getName() + " and annotation " + annotation.getName() + ", there's already a registered parameter transformer!");
        }
        parameterTransformers.computeIfAbsent(clazz, aClass -> new HashMap<>()).put(annotation, parameterProvider);
    }

    @Override
    public <T> boolean hasRegisteredTransformer(@NotNull Class<T> clazz, Class<?> annotationType) {
        return parameterTransformers.computeIfAbsent(clazz, aClass -> new HashMap<>()).containsKey(annotationType);
    }

    @NotNull
    @Override
    public Map<Class<?>, Map<Class<?>, ParameterProvider>> getRegisteredParameterTransformers() {
        return parameterTransformers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParameterProvider<T> getParameterTransformer(@NotNull Class<T> clazz, @Nullable Class<?> annotationType) {
        return (ParameterProvider<T>) parameterTransformers.computeIfAbsent(clazz, aClass -> new HashMap<>()).get(annotationType);
    }
}
