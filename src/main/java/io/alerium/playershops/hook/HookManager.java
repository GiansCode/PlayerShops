package io.alerium.playershops.hook;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.hook.hooks.CitizensHook;
import io.alerium.playershops.hook.hooks.EconomyHook;
import io.alerium.playershops.hook.hooks.WorldGuardHook;
import lombok.Getter;

public class HookManager {
    
    private final PlayerShops plugin;

    @Getter private EconomyHook economyHook;
    @Getter private WorldGuardHook worldGuardHook;
    @Getter private CitizensHook citizensHook;
    
    public HookManager(PlayerShops plugin) {
        this.plugin = plugin;
    }

    public boolean init() {
        economyHook = new EconomyHook(plugin);
        worldGuardHook = new WorldGuardHook(plugin);
        citizensHook = new CitizensHook(plugin);

        return economyHook.init() && worldGuardHook.init() && citizensHook.init();
    }
    
}
