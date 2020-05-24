package io.alerium.playershops.menu.buy;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.menu.management.ConfirmationMenu;
import io.alerium.playershops.menu.management.items.ItemSelectorMenu;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BuyMenu extends ItemSelectorMenu
{
    public BuyMenu(PlayerShops plugin, Shop shop, MenuData menuData)
    {
        super(plugin, shop, menuData, shop.getName());
    }

    @Override
    public boolean onClick(Player player, ItemStack itemStack, ClickType click, int slot)
    {
        ItemStack stack = originalItems[slot];
        double price = shop.getPrice(stack);

        if (!plugin.getHooks().getEconomyHook().canAfford(player, price))
        {
            Message.send(player, Message.SHOP_CANNOT_AFFORD_BUY, shop.getName());
            player.closeInventory();

            return false;
        }

        new ConfirmationMenu(plugin, shop, new MenuData(plugin, "confirm"), "Buy Item", shop ->
            shop.buy(player, stack, price)
        ).open(player);
        return false;
    }
}
