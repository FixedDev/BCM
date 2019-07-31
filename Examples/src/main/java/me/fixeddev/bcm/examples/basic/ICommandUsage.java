package me.fixeddev.bcm.examples.basic;

import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.Authorizer;
import me.fixeddev.bcm.basic.BasicCommandHandler;
import me.fixeddev.bcm.basic.ICommand;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICommandUsage {

    public static void main(String[] args) {
        Authorizer authorizer = new SenderAuthorizer(); // An authorized is what checks if a specific sender has permission
        PermissionMessageProvider messageProvider = new NoOpPermissionMessageProvider(); // This is for i18n no permission support
        Logger logger = Logger.getLogger("CommandsLog"); // A logger to log the commmands registration

        BasicCommandHandler handler = new BasicCommandHandler(authorizer, messageProvider, logger); // The class that registers the commands and dispatch them

        handler.registerCommand(new TestCommand()); // This registers the command "test" into the handler, now it can be dispatched by this instance

        Namespace namespace = new Namespace(); // This class pass to the command some pre created objects like the sender
        namespace.setObject(Sender.class, "sender", new Sender("FixedDev")); // This puts the object sender into the namespace, needed for our specific authorizer

        try {
            handler.dispatchCommand(namespace, "test");
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


    public static class TestCommand implements ICommand {

        @Override
        public String[] getNames() {
            return new String[]{"test"};
        }

        @Override
        public List<ICommand> getSubCommands() {
            return new ArrayList<>();
        }

        @Override
        public boolean run(Namespace namespace, ArgumentArray arguments) throws CommandException, NoPermissionsException, NoMoreArgumentsException, ArgumentsParseException {
            if(arguments.getSize() < 0){
                return false; // The command needs at least one argument, send the usage to the sender
            }

            System.out.println("This works :D");

            return true; // It executed the command right, don't send the usage to the sender
        }
    }

}
