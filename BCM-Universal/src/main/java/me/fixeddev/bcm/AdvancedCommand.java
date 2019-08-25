package me.fixeddev.bcm;

import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.ICommand;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

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

    boolean execute(CommandContext context) throws CommandException, NoPermissionsException,  ArgumentsParseException;

    @Override
    default boolean run(Namespace namespace, ArgumentArray arguments) throws CommandException, NoPermissionsException,  ArgumentsParseException {
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
