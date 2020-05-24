package io.alerium.playershops.util;

import org.bukkit.entity.Player;

public final class UtilPermission {

    private UtilPermission() {
    }

    public static boolean hasPermission(Player player, String permission) {
        return player.isOp() || player.hasPermission("playershops.*") || player.hasPermission(permission);
    }
    
}
