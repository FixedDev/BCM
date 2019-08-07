package me.fixeddev.bcm.basic;

import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

public interface DelegatedICommand extends ICommand, DelegatedCommand<ICommand> {

    @Override
    default boolean run(Namespace namespace, ArgumentArray arguments) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
        return getDelegate().run(namespace, arguments);
    }
}
