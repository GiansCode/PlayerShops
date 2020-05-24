package io.alerium.playershops.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Placeable
{
    boolean onPlace(Player player, ItemStack stack);
}
