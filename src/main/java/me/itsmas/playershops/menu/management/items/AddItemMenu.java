package me.itsmas.playershops.menu.management.items;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.menu.Menu;
import me.itsmas.playershops.menu.MenuButton;
import me.itsmas.playershops.menu.MenuData;
import me.itsmas.playershops.menu.Placeable;
import me.itsmas.playershops.shop.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AddItemMenu extends Menu implements Placeable
{
    private final MenuButton filler;
    private final MenuButton itemSlot;

    private final MenuData setPriceMenuData;

    AddItemMenu(PlayerShops plugin, Shop shop, MenuData menuData, MenuData setPriceMenuData)
    {
        super(plugin, shop, menuData);

        this.filler = menuData.getButton("filler");
        this.itemSlot = menuData.getButton("item_slot");

        this.setPriceMenuData = setPriceMenuData;
    }

    @Override
    public void open(Player player)
    {
        addItems();

        super.open(player);
    }

    private void addItems()
    {
        addButton(itemSlot);

        for (int i = 0; i < inventory.getSize(); i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                setItem(i, filler.getStack());
            }
        }

        setItem(itemSlot.getSlot(), null);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        return false;
    }

    @Override
    public boolean onPlace(Player player, ItemStack stack)
    {
        ItemStack clone = stack.clone();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                new SetPriceMenu(plugin, shop, setPriceMenuData, clone, true).open(player);
            }
        }.runTaskLater(plugin, 1L);

        return true;
    }
}
