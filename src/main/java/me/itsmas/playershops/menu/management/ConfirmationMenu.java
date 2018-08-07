package me.itsmas.playershops.menu.management;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.menu.Menu;
import me.itsmas.playershops.menu.MenuButton;
import me.itsmas.playershops.menu.MenuData;
import me.itsmas.playershops.shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ConfirmationMenu extends Menu
{
    private final MenuButton confirm;
    private final MenuButton cancel;

    private final Consumer<Shop> confirmAction;

    public ConfirmationMenu(PlayerShops plugin, Shop shop, MenuData menuData, String action, Consumer<Shop> confirmAction)
    {
        super(plugin, shop, menuData.formatName(action));

        this.confirm = menuData.getButton("confirm");
        this.cancel = menuData.getButton("cancel");

        this.confirmAction = confirmAction;

        addButton(confirm);
        addButton(cancel);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        if (wasClicked(stack, confirm))
        {
            confirmAction.accept(shop);
        }

        player.closeInventory();

        return false;
    }
}
