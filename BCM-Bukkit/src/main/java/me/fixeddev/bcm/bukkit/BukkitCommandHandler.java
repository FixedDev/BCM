package me.fixeddev.bcm.bukkit;

import me.fixeddev.bcm.basic.ICommand;
import me.fixeddev.bcm.basic.PermissionMessageProvider;
import me.fixeddev.bcm.bukkit.providers.BukkitModule;

import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class BukkitCommandHandler extends ParametricCommandHandler {

    private Map<ICommand, BukkitCommandWrapper> commandWrapperMap;

    private CommandMap commandMap;

    public BukkitCommandHandler(Logger logger, PermissionMessageProvider messageProvider, ParameterProviderRegistry registry) {
        super(new CommandSenderAuthorizer(), messageProvider, registry, logger);

        registry.installModule(new BukkitModule());

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
