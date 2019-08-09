package me.fixeddev.bcm.bukkit.providers;

import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;
import me.fixeddev.bcm.parametric.providers.ProvidersModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitModule implements ProvidersModule {
    @Override
    public void configure(ParameterProviderRegistry registry) {
        registry.registerParameterTransfomer(CommandSender.class, new CommandSenderProvider());
        registry.registerParameterTransfomer(OfflinePlayer.class, new OfflinePlayerProvider());
        registry.registerParameterTransfomer(Player.class, new PlayerProvider());
    }
}
