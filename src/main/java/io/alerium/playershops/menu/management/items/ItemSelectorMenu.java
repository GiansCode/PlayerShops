package io.alerium.playershops.menu.management.items;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.Menu;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.shop.Shop;
import io.alerium.playershops.util.UtilItem;
import io.alerium.playershops.util.UtilString;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ItemSelectorMenu extends Menu {
    
    protected ItemStack[] originalItems;
    
    public ItemSelectorMenu(PlayerShops plugin, Shop shop, MenuData menuData, Object... params) {
        super(plugin, shop, shop.getGuiSize(), menuData.getName().contains("%s") ? String.format(menuData.getName(), params) : menuData.getName());

        addItems();
    }

    private void addItems() {
        originalItems = shop.getStacks().toArray(new ItemStack[0]);

        Map<ItemStack, Double> items = shop.getItems();
        AtomicInteger index = new AtomicInteger(-1);

        String format = UtilString.colour(plugin.getConfig("price_format"));

        items.forEach((stack, price) -> setItem(index.incrementAndGet(), UtilItem.addLore(stack.clone(), String.format(format, price))));
    }
    
}
