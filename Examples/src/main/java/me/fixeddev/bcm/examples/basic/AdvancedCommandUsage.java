package me.fixeddev.bcm.examples.basic;

import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.basic.Authorizer;
import me.fixeddev.bcm.basic.BasicCommandHandler;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.NoOpPermissionMessageProvider;
import me.fixeddev.bcm.basic.PermissionMessageProvider;
import me.fixeddev.bcm.basic.exceptions.ArgumentsParseException;
import me.fixeddev.bcm.basic.exceptions.CommandException;
import me.fixeddev.bcm.basic.exceptions.CommandUsageException;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;
import me.fixeddev.bcm.examples.Sender;
import me.fixeddev.bcm.examples.SenderAuthorizer;


import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvancedCommandUsage {

    public static void main(String[] args) {
        Authorizer authorizer = new SenderAuthorizer(); // An authorized is what checks if a specific sender has permission
        PermissionMessageProvider messageProvider = new NoOpPermissionMessageProvider(); // This is for i18n no permission support
        Logger logger = Logger.getLogger("CommandsLog"); // A logger to log the commmands registration

        BasicCommandHandler handler = new BasicCommandHandler(authorizer, messageProvider, logger); // The class that registers the commands and dispatch them

        handler.registerCommand(new TestAdvancedCommand()); // This registers the command "test" into the handler, now it can be dispatched by this instance

        Namespace namespace = new Namespace(); // This class pass to the command some pre created objects like the sender
        namespace.setObject(Sender.class, "sender", new Sender("FixedDev")); // This puts the object sender into the namespace, needed for our specific authorizer

        try {
            handler.dispatchCommand(namespace, "test ola -f");

            handler.dispatchCommand(namespace, "test ola");
        } catch (CommandException e) {
            logger.log(Level.SEVERE, "The command failed to execute ;(", e);
        } catch (NoPermissionsException e) {
            logger.log(Level.WARNING, e.getMessage()); // The NoPermissionsException is throwed when the authorizer returns false, the message of the exception is a no permission message
        } catch (CommandUsageException e) {
            logger.log(Level.WARNING, "The correct usage is:" + e.getCommand().getUsage());
        } catch (ArgumentsParseException e) {
            logger.log(Level.WARNING, "Failed to parse the commands argument", e); // This can be caused for many reasons, it's an error
        }
    }

    // I recommend you to extend AbstractAdvancedCommand instead of implementing AdvancedCommand
    public static class TestAdvancedCommand extends AbstractAdvancedCommand {

        public TestAdvancedCommand() {
            super(new String[]{"test"});

            setMaxArguments(1);
            setMinArguments(1);
            setUsage("<command> <message>");
            setAllowAnyFlags(true);
        }

        @Override
        public boolean execute(CommandContext context) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
            // A command context is a more complex form of the command arguments

            if (context.getFlagValue('f')) {
                System.out.println("Executed command with flag f");
            }

            System.out.println("Test message: " + context.getArgument(0));

            return true;
        }
    }
}
