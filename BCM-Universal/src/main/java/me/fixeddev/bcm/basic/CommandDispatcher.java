package me.fixeddev.bcm.basic;

import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandUsageException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

import java.util.List;

public interface CommandDispatcher {
    Authorizer getAuthorizer();

    PermissionMessageProvider getPermissionMessageProvider();

    CommandRegistry getRegistry();

    boolean dispatchCommand(Namespace namespace, String command) throws CommandException, NoPermissionsException, CommandUsageException, ArgumentsParseException;

    List<String> getCommandSuggestions(Namespace namespace, String command) throws CommandException, NoPermissionsException, NoMoreArgumentsException;
}
