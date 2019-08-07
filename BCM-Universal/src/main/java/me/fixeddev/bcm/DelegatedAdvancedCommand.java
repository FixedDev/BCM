package me.fixeddev.bcm;

import me.fixeddev.bcm.basic.DelegatedCommand;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;

public interface DelegatedAdvancedCommand extends AdvancedCommand, DelegatedCommand<AdvancedCommand> {
    @Override
    default boolean execute(CommandContext context) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
        return getDelegate().execute(context);
    }
}
