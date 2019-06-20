package me.fixeddev.bcm.bukkit.providers;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.parametric.ParameterProvider;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;

public class CommandSenderProvider implements ParameterProvider<CommandSender> {
    @Override
    public CommandSender transformParameter(ArgumentStack arguments, Namespace namespace, Annotation modifiers, String defaultValue) throws NoMoreArgumentsException, ArgumentsParseException {
        return namespace.getObject(CommandSender.class, "sender");
    }
}
