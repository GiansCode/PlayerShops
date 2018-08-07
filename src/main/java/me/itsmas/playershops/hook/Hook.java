package me.itsmas.playershops.hook;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.util.Logs;
import org.bukkit.Bukkit;

public abstract class Hook
{
    protected final PlayerShops plugin;
    private final String[] requiredPlugins;

    protected Hook(PlayerShops plugin, String... requiredPlugins)
    {
        this.plugin = plugin;
        this.requiredPlugins = requiredPlugins;
    }

    protected boolean init()
    {
        for (String plugin : requiredPlugins)
        {
            if (!Bukkit.getPluginManager().isPluginEnabled(plugin))
            {
                Logs.error("Required plugin " + plugin + " not found or enabled");
                return false;
            }
        }

        return true;
    }
}