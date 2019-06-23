package me.itsmas.playershops.hook.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
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

    private WorldGuard worldGuard;

    @Override
    public boolean init()
    {
        if (!super.init())
        {
            return false;
        }

        worldGuard = WorldGuard.getInstance();
        return worldGuard != null;
    }

    public boolean canBuild(Player player, Location location)
    {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        return worldGuard
            .getPlatform()
            .getRegionContainer()
            .createQuery()
            .testBuild(BukkitAdapter.adapt(location), localPlayer);
    }
}
