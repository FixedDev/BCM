package me.ggamer55.bcm.bukkit;

import me.ggamer55.bcm.parametric.*;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.Parameter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {

    private BukkitCommandHandler dispatcher;

    @Override
    public void onEnable() {
        dispatcher = new BukkitCommandHandler(this.getLogger());

        dispatcher.registerCommandClass(new TestCommand());
    }

    @Override
    public void onDisable() {
        dispatcher.unregisterAllCommands();
    }


    class TestCommand implements CommandClass {

        @Command(names = "test hola", min = 1, usage = "/<command> <hello> <player>")
        public boolean testCommand(@Parameter("sender") CommandSender sender, @Parameter("hello") boolean hello, @Parameter("target") Player player) {
            sender.sendMessage("hello: " + hello);
            sender.sendMessage("Player: " + player);
            return true;
        }
    }

}
