package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.basic.CommandRegistry;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;
import org.jetbrains.annotations.NotNull;

public interface ParametricCommandRegistry extends CommandRegistry {
    void registerCommandClass(CommandClass commandClass);

    @NotNull
    ParameterProviderRegistry getParameterProviderRegistry();
}
