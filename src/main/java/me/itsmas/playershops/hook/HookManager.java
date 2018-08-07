package me.itsmas.playershops.hook;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.hook.hooks.CitizensHook;
import me.itsmas.playershops.hook.hooks.EconomyHook;
import me.itsmas.playershops.hook.hooks.WorldGuardHook;

public class HookManager
{
    private final PlayerShops plugin;

    public HookManager(PlayerShops plugin)
    {
        this.plugin = plugin;
    }

    private EconomyHook economyHook; public EconomyHook getEconomyHook() { return economyHook; }
    private WorldGuardHook worldGuardHook; public WorldGuardHook getWorldGuardHook() { return worldGuardHook; }
    private CitizensHook citizensHook; public CitizensHook getCitizensHook() { return citizensHook; }

    public boolean init()
    {
        economyHook = new EconomyHook(plugin);
        worldGuardHook = new WorldGuardHook(plugin);
        citizensHook = new CitizensHook(plugin);

        return economyHook.init() && worldGuardHook.init() && citizensHook.init();
    }
}
