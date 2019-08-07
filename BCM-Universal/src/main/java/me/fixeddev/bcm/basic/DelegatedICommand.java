package me.fixeddev.bcm.basic;

import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

import java.util.ArrayList;
import java.util.List;

public interface DelegatedICommand extends ICommand, DelegatedCommand<ICommand> {

    @Override
    default boolean run(Namespace namespace, ArgumentArray arguments) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
        ICommand delegate = getDelegate();

        if(delegate == null){
            return true;
        }

        return delegate.run(namespace, arguments);
    }

    @Override
    default List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) throws CommandException, NoMoreArgumentsException {
        ICommand delegate = getDelegate();

        if(delegate == null){
            return new ArrayList<>();
        }

        return delegate.getSuggestions(namespace, arguments);
    }
}
