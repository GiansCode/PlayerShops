package me.itsmas.playershops.menu.management.items;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.menu.MenuData;
import me.itsmas.playershops.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ChangePriceMenu extends ItemSelectorMenu
{
    ChangePriceMenu(PlayerShops plugin, Shop shop, MenuData menuData)
    {
        super(plugin, shop, menuData);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        new SetPriceMenu(plugin, shop, new MenuData(plugin, "set_price"), originalItems[slot], false).open(player);
        return false;
    }
}
