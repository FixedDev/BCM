package me.ggamer55.bcm.bukkit.providers;

import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.parametric.ParameterProvider;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.util.List;

public class CommandSenderProvider implements ParameterProvider<CommandSender> {
    @Override
    public CommandSender transformParameter(ArgumentStack arguments, Namespace namespace, List<Annotation> modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        return namespace.getObject(CommandSender.class, "sender");
    }
}
