package io.alerium.playershops.menu.management;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.Menu;
import io.alerium.playershops.menu.MenuButton;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.menu.management.items.ManageItemsMenu;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.shop.Shop;
import io.alerium.playershops.util.UtilPermission;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ManagementMenu extends Menu
{
    private final MenuData confirmMenuData;
    private final MenuData entityTypeMenuData;
    private final MenuData manageItemsMenuData;

    private final MenuButton rename;
    private final MenuButton remove;
    private final MenuButton move;
    private final MenuButton manageItems;
    private final MenuButton changeEntityType;

    public ManagementMenu(PlayerShops plugin, Shop shop, MenuData menuData, MenuData confirmMenuData, MenuData entityTypeMenuData, MenuData manageItemsMenuData)
    {
        super(plugin, shop, menuData);

        this.rename = menuData.getButton("rename");
        this.remove = menuData.getButton("remove");
        this.move = menuData.getButton("move");
        this.manageItems = menuData.getButton("manage_shop_items");
        this.changeEntityType = menuData.getButton("change_entity_type");

        this.confirmMenuData = confirmMenuData;
        this.entityTypeMenuData = entityTypeMenuData;
        this.manageItemsMenuData = manageItemsMenuData;

        addButton(rename);
        addButton(remove);
        addButton(move);
        addButton(manageItems);
        addButton(changeEntityType);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        Menu menu = null;

        if (wasClicked(stack, rename))
        {
            new AnvilGUI(plugin, player, shop.getName(), (p, reply) ->
            {
                String adaptedName = reply.replace("Shop Name Here", player.getName()).replaceAll("\\s+", " ");

                if (UtilPermission.hasPermission(player, "playershops.shop_name_colour"))
                {
                    adaptedName = ChatColor.translateAlternateColorCodes('&', adaptedName);
                }

                if (adaptedName.length() > 16)
                {
                    Message.send(player, Message.SHOP_NAME_LENGTH, shop.getName());
                    return null;
                }

                if (shop.getName().equals(adaptedName))
                {
                    Message.send(player, Message.SHOP_NAME_IDENTICAL, shop.getName());
                    return null;
                }

                if (!plugin.getShopManager().canNameShop(player, adaptedName))
                {
                    Message.send(player, Message.SHOP_NAME_EXISTS, shop.getName());
                    return null;
                }

                shop.rename(adaptedName);
                return null;
            });
        }
        else if (wasClicked(stack, remove))
        {
            menu = new ConfirmationMenu(plugin, shop, confirmMenuData, "Remove Shop", Shop::remove);
        }
        else if (wasClicked(stack, move))
        {
            plugin.getPlaceListener().addMove(player, shop);

            Message.send(player, Message.SHOP_MOVE);
            player.closeInventory();
        }
        else if (wasClicked(stack, manageItems))
        {
            menu = new ManageItemsMenu(plugin, shop, manageItemsMenuData);
        }
        else if (wasClicked(stack, changeEntityType))
        {
            menu = new EntityTypeMenu(plugin, shop, entityTypeMenuData);
        }

        if (menu != null)
        {
            menu.open(player);
        }

        return false;
    }
}
