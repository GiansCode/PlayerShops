package io.alerium.playershops.hook.hooks;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.hook.Hook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.Optional;

public class WorldGuardHook extends Hook {
    
    public WorldGuardHook(PlayerShops plugin) {
        super(plugin, "WorldEdit", "WorldGuard");
    }

    private WorldGuardWrapper worldGuard;
    private IWrappedFlag<WrappedState> buildFlag;
    
    @Override
    public boolean init() {
        if (!super.init())
            return false;

        worldGuard = WorldGuardWrapper.getInstance();
        if (worldGuard == null)
            return false;
        
        Optional<IWrappedFlag<WrappedState>> flag = worldGuard.getFlag("build", WrappedState.class);
        if (!flag.isPresent())
            return false;
        
        buildFlag = flag.get();
        return true;
    }

    public boolean canBuild(Player player, Location location) {
        Optional<WrappedState> state = worldGuard.queryFlag(player, location, buildFlag);
        return state.isPresent() && state.get() == WrappedState.ALLOW;
    }
}
