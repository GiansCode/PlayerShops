package me.itsmas.playershops.menu.management.items;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.menu.Menu;
import me.itsmas.playershops.menu.MenuButton;
import me.itsmas.playershops.menu.MenuData;
import me.itsmas.playershops.message.Message;
import me.itsmas.playershops.shop.Shop;
import me.itsmas.playershops.util.UtilPermission;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class ManageItemsMenu extends Menu
{
    private static MenuData addItemMenuData;
    private static MenuData setPriceMenuData;
    private static MenuData removeItemMenuData;

    private final MenuButton addItem;
    private final MenuButton modifyPrice;
    private final MenuButton removeItem;

    public ManageItemsMenu(PlayerShops plugin, Shop shop, MenuData menuData)
    {
        super(plugin, shop, menuData);

        addItem = menuData.getButton("add_item");
        modifyPrice = menuData.getButton("modify_price");
        removeItem = menuData.getButton("remove_item");

        addButton(addItem);
        addButton(modifyPrice);
        addButton(removeItem);

        if (addItemMenuData == null)
        {
            addItemMenuData = new MenuData(plugin, "add_item");
            removeItemMenuData = new MenuData(plugin, "remove_item");
            setPriceMenuData = new MenuData(plugin, "set_price");
        }
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        if (wasClicked(stack, addItem))
        {
            int max = getMaxItems(player);

            if (shop.getStacks().size() < max)
            {
                new AddItemMenu(plugin, shop, addItemMenuData, setPriceMenuData).open(player);
            }
            else
            {
                Message.send(player, Message.SHOP_ITEM_LIMIT, shop.getName());
            }
        }
        else if (wasClicked(stack, modifyPrice))
        {
            if (shop.getStacks().size() == 0)
            {
                Message.send(player, Message.SHOP_NO_ITEMS_TO_MODIFY, shop.getName());
                return false;
            }

            new ChangePriceMenu(plugin, shop, setPriceMenuData).open(player);
        }
        else if (wasClicked(stack, removeItem))
        {
            if (shop.getStacks().size() == 0)
            {
                Message.send(player, Message.SHOP_NO_ITEMS_TO_REMOVE, shop.getName());
                return false;
            }

            new RemoveItemMenu(plugin, shop, removeItemMenuData).open(player);
        }

        return false;
    }

    private int getMaxItems(Player player)
    {
        int highest = 0;

        if (UtilPermission.hasPermission(player, null))
        {
            highest = Integer.MAX_VALUE;
        }
        else
        {
            for (PermissionAttachmentInfo permission : player.getEffectivePermissions())
            {
                String permissionName = permission.getPermission();

                if (permissionName.matches("playershops.item_limit.\\d+"))
                {
                    int limit = Integer.parseInt(permissionName.split("\\.")[2]);

                    if (limit > highest)
                    {
                        highest = limit;
                    }
                }
            }
        }

        return highest;
    }
}
