package me.fixeddev.bcm.parametric.providers;

import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.parametric.annotation.JoinedString;
import me.fixeddev.bcm.parametric.providers.defaults.BooleanProvider;
import me.fixeddev.bcm.parametric.providers.defaults.DoubleProvider;
import me.fixeddev.bcm.parametric.providers.defaults.IntegerProvider;
import me.fixeddev.bcm.parametric.providers.defaults.JoinedStringProvider;
import me.fixeddev.bcm.parametric.providers.defaults.NamespaceProvider;
import me.fixeddev.bcm.parametric.providers.defaults.StringParameterProvider;

public class DefaultsModule implements ProvidersModule {
    @Override
    public void configure(ParameterProviderRegistry registry) {
        registry.registerParameterTransfomer(Namespace.class, new NamespaceProvider());
        registry.registerParameterTransfomer(String.class, new StringParameterProvider());
        registry.registerParameterTransformer(String.class, JoinedString.class, new JoinedStringProvider());

        registry.registerParameterTransfomer(boolean.class, new BooleanProvider());
        registry.registerParameterTransfomer(Boolean.class, new BooleanProvider());

        registry.registerParameterTransfomer(double.class, new DoubleProvider());
        registry.registerParameterTransfomer(Double.class, new DoubleProvider());

        registry.registerParameterTransfomer(int.class, new IntegerProvider());
        registry.registerParameterTransfomer(Integer.class, new IntegerProvider());
    }
}
