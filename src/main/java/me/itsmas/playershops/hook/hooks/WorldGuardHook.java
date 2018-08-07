package me.itsmas.playershops.hook.hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.hook.Hook;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook extends Hook
{
    public WorldGuardHook(PlayerShops plugin)
    {
        super(plugin, "WorldEdit", "WorldGuard");
    }

    private WorldGuardPlugin worldGuard;

    @Override
    public boolean init()
    {
        if (!super.init())
        {
            return false;
        }

        worldGuard = WorldGuardPlugin.inst();
        return worldGuard != null;
    }

    public boolean canBuild(Player player, Location location)
    {
        return worldGuard.canBuild(player, location);
    }
}
