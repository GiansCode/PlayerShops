package io.alerium.playershops.menu.management.items;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.Menu;
import io.alerium.playershops.menu.MenuButton;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.menu.Placeable;
import io.alerium.playershops.shop.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AddItemMenu extends Menu implements Placeable {
    
    private final MenuButton filler;
    private final MenuButton itemSlot;

    private final MenuData setPriceMenuData;

    AddItemMenu(PlayerShops plugin, Shop shop, MenuData menuData, MenuData setPriceMenuData) {
        super(plugin, shop, menuData);

        this.filler = menuData.getButton("filler");
        this.itemSlot = menuData.getButton("item_slot");

        this.setPriceMenuData = setPriceMenuData;
    }

    @Override
    public void open(Player player) {
        addItems();

        super.open(player);
    }

    private void addItems() {
        addButton(itemSlot);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
                setItem(i, filler.getStack());
        }

        setItem(itemSlot.getSlot(), null);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot) {
        return false;
    }

    @Override
    public boolean onPlace(Player player, ItemStack stack) {
        ItemStack clone = stack.clone();

        new BukkitRunnable() {
            @Override
            public void run() {
                new SetPriceMenu(plugin, shop, setPriceMenuData, clone, true).open(player);
            }
        }.runTaskLater(plugin, 1L);
        return true;
    }
    
}
