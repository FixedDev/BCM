package me.ggamer55.bcm;

import me.ggamer55.bcm.basic.ArgumentArray;
import me.ggamer55.bcm.basic.ArgumentStack;
import me.ggamer55.bcm.basic.ICommand;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.CommandException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.basic.exceptions.NoPermissionsException;

import java.util.ArrayList;
import java.util.List;

public interface AdvancedCommand extends ICommand {

    default int getMinArguments() {
        return 0;
    }

    default int getMaxArguments() {
        return -1;
    }

    default boolean allowAnyFlags() {
        return false;
    }

    default List<Character> getExpectedFlags() {
        return new ArrayList<>();
    }

    void registerSubCommand(ICommand command);

    void unregisterSubCommand(ICommand command);

    boolean execute(CommandContext context) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException;

    @Override
    default boolean run(Namespace namespace, ArgumentArray arguments) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
        if (arguments.getSize() < getMinArguments()) {
            return false;
        }

        if (getMaxArguments() != -1 && arguments.getSize() > getMaxArguments()) {
            return false;
        }

        CommandContext commandContext = new CommandContext(getNames()[0],
                namespace.getObject(String.class, "label"),
                arguments,
                getExpectedFlags(),
                allowAnyFlags(),
                namespace);

        return execute(commandContext);
    }
}
