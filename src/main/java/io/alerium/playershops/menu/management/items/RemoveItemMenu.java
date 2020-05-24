package io.alerium.playershops.menu.management.items;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.menu.management.ConfirmationMenu;
import io.alerium.playershops.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RemoveItemMenu extends ItemSelectorMenu {
    
    RemoveItemMenu(PlayerShops plugin, Shop shop, MenuData menuData) {
        super(plugin, shop, menuData);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot) {
        new ConfirmationMenu(plugin, shop, new MenuData(plugin, "confirm"), "Remove Item", shop -> shop.removeItem(originalItems[slot], false)).open(player);
        return false;
    }
    
}
