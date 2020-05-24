package io.alerium.playershops.menu;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.shop.Shop;
import io.alerium.playershops.util.UtilItem;
import io.alerium.playershops.util.UtilString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Menu {
    
    protected final PlayerShops plugin;

    protected final Shop shop;
    protected final Inventory inventory;

    public Menu(PlayerShops plugin, Shop shop, MenuData menuData) {
        this(plugin, shop, menuData.getSize(), menuData.getName());
    }

    public Menu(PlayerShops plugin, Shop shop, int size, String name) {
        this.plugin = plugin;

        this.shop = shop;
        this.inventory = Bukkit.createInventory(null, size * 9, UtilString.colour(name));
    }

    protected void setItem(int index, ItemStack stack) {
        inventory.setItem(index, stack);
    }

    public void open(Player player) {
        player.openInventory(inventory);
        plugin.getMenuManager().updateMenu(player, this);
    }

    protected void addButton(MenuButton button) {
        setItem(button.getSlot(), button.getStack());
    }

    /**
     * @return True if the player can take the item
     */
    public abstract boolean onClick(Player player, ItemStack stack, ClickType click, int slot);

    protected boolean wasClicked(ItemStack stack, MenuButton button) {
        return
                stack.getType() == button.getStack().getType() &&
                        UtilItem.getItemName(stack).equals(UtilItem.getItemName(button.getStack()));
    }
}
