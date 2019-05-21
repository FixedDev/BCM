package me.fixeddev.bcm.basic;

import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

import java.util.ArrayList;
import java.util.List;

public interface ICommand {

    String[] getNames();

    default String getUsage() {
        return "/<command>";
    }

    default String getDescription() {
        return "";
    }

    default String getPermission() {
        return "";
    }

    default String getPermissionMessage() {
        return "No Permission.";
    }

    default boolean isPermissionMessageOverride() {
        return false;
    }

    List<ICommand> getSubCommands();

    boolean run(Namespace namespace, ArgumentArray arguments) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException;

    default List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) throws CommandException, NoMoreArgumentsException {
        return new ArrayList<>();
    }
}
