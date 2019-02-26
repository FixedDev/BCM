package me.ggamer55.bcm.bukkit;

import me.ggamer55.bcm.basic.ICommand;
import me.ggamer55.bcm.bukkit.providers.CommandSenderProvider;
import me.ggamer55.bcm.bukkit.providers.OfflinePlayerProvider;
import me.ggamer55.bcm.bukkit.providers.PlayerProvider;
import me.ggamer55.bcm.parametric.ParametricCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class BukkitCommandHandler extends ParametricCommandHandler {

    private Map<ICommand, BukkitCommandWrapper> commandWrapperMap;

    private CommandMap commandMap;

    public BukkitCommandHandler(Logger logger) {
        super(new CommandSenderAuthorizer(), logger);

        registerParameterTransfomer(CommandSender.class, new CommandSenderProvider());
        registerParameterTransfomer(OfflinePlayer.class, new OfflinePlayerProvider());
        registerParameterTransfomer(Player.class, new PlayerProvider());

        commandWrapperMap = new ConcurrentHashMap<>();

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            Bukkit.getLogger().severe("Failed to get command map: " + ex.getMessage());
        }
    }


    @Override
    public void registerCommand(ICommand command) {
        super.registerCommand(command);

        BukkitCommandWrapper wrapper = new BukkitCommandWrapper(command, this);
        commandWrapperMap.put(command, wrapper);

        commandMap.register(command.getNames()[0], wrapper);
    }

    @Override
    public void unregisterCommand(ICommand command) {
        super.unregisterCommand(command);

        BukkitCommandWrapper wrapper = commandWrapperMap.get(command);

        if(wrapper == null){
            return;
        }

        wrapper.unregister(commandMap);
    }


}
