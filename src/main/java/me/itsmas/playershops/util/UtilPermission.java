package me.itsmas.playershops.util;

import org.bukkit.entity.Player;

public class UtilPermission
{
    public static boolean hasPermission(Player player, String permission)
    {
        return
            player.isOp() ||
            player.hasPermission("playershops.*") ||
            player.hasPermission(permission);
    }
}
