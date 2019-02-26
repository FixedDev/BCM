package me.ggamer55.bcm.basic;

import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.CommandException;
import me.ggamer55.bcm.basic.exceptions.CommandUsageException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.basic.exceptions.NoPermissionsException;

import java.util.List;

public interface CommandDispatcher {
    Authorizer getAuthorizer();

    CommandRegistry getRegistry();

    boolean dispatchCommand(Namespace namespace, String command) throws CommandException, NoPermissionsException, CommandUsageException, ArgumentsParseException;

    List<String> getCommandSuggestions(Namespace namespace, String command) throws CommandException, NoPermissionsException, NoMoreArgumentsException;
}
