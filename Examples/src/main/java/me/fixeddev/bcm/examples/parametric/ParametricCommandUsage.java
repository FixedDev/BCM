package me.fixeddev.bcm.examples.parametric;

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
import me.fixeddev.bcm.basic.exceptions.NoPermissionsException;
import me.fixeddev.bcm.examples.Sender;
import me.fixeddev.bcm.examples.SenderAuthorizer;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Flag;
import me.fixeddev.bcm.parametric.annotation.JoinedString;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ParametricCommandUsage {
    public static void main(String[] args) {
        Authorizer authorizer = new SenderAuthorizer(); // An authorized is what checks if a specific sender has permission
        PermissionMessageProvider messageProvider = new NoOpPermissionMessageProvider(); // This is for i18n no permission support
        Logger logger = Logger.getLogger("CommandsLog"); // A logger to log the commmands registration

        logger.setLevel(Level.ALL);

        // Create the registry for the parameter providers
        ParameterProviderRegistry registry = ParameterProviderRegistry.createRegistry();
        // Register the sender provider
        registry.registerParameterTransfomer(Sender.class, new SenderProvider());

        ParametricCommandHandler handler = new ParametricCommandHandler(authorizer, messageProvider, registry, logger); // The class that registers the parametric commands and dispatch them

        handler.registerCommandClass(new TestCommandClass()); // This registers all the commands of that class into the handler, now they can be dispatched by this instance

        Namespace namespace = new Namespace(); // This class pass to the command some pre created objects like the sender
        namespace.setObject(Sender.class, "sender", new Sender("FixedDev")); // This puts the object sender into the namespace, needed for our specific authorizer

        try {
            handler.dispatchCommand(namespace, "test ola");
            handler.dispatchCommand(namespace, "test subcommand ola");
            handler.dispatchCommand(namespace, "test withflag -f");
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


    public static class TestCommandClass implements CommandClass {

        @Command(names = "test")
        public boolean testCommand(Sender sender, @JoinedString String message) {
            System.out.println("Hello " + sender.getName() + " " + message);

            return true;
        }

        @Command(names = "test subcommand")
        public boolean testSubCommandCommand(Sender sender, @JoinedString String message) {
            System.out.println("Hello " + sender.getName() + " from a subcommand " + message);

            return true;
        }

        @Command(names = "test withFlag")
        public boolean testSubCommandCommand(Sender sender, @Flag('f') boolean flag) {
            System.out.println("Hello " + sender.getName() + " flag " + flag);

            return true;
        }
    }

}
