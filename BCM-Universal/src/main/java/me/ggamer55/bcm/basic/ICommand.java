package me.ggamer55.bcm.basic;

import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.CommandException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;
import me.ggamer55.bcm.basic.exceptions.NoPermissionsException;

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
