package me.itsmas.playershops.util;

import me.itsmas.playershops.PlayerShops;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilServer
{
    private static final PlayerShops plugin = JavaPlugin.getPlugin(PlayerShops.class);

    static PlayerShops getPlugin()
    {
        return plugin;
    }

    public static void registerListener(Listener listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public static void dispatchCommand(String command)
    {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
