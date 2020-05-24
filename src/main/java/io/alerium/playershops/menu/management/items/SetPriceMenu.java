package io.alerium.playershops.menu.management.items;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.CloseListener;
import io.alerium.playershops.menu.Menu;
import io.alerium.playershops.menu.MenuButton;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.shop.Shop;
import io.alerium.playershops.util.UtilItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class SetPriceMenu extends Menu implements CloseListener
{
    private final MenuButton sellingItem;
    private final MenuButton currentPrice;
    private final MenuButton confirm;

    private final Set<MenuButton> priceModifiers;

    private final ItemStack stack;

    private final boolean newItem;

    SetPriceMenu(PlayerShops plugin, Shop shop, MenuData menuData, ItemStack stack, boolean newItem)
    {
        super(plugin, shop, menuData);

        this.sellingItem = menuData.getButton("selling_item");
        this.currentPrice = menuData.getButton("current_price");
        this.confirm = menuData.getButton("confirm");

        this.priceModifiers = menuData.getButtons("add_", "remove_");
        priceModifiers.forEach(this::addButton);

        this.stack = stack;

        this.newItem = newItem;

        setPrice(0.0D);

        setInitialLore();
        addButton(currentPrice);

        addButton(confirm);

        setItem(sellingItem.getSlot(), stack);
    }

    private double price;
    private boolean added = false;

    @Override
    public void open(Player player)
    {
        super.open(player);
    }

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        MenuButton modifier = getClickedButton(stack);

        if (modifier != null)
        {
            double adjustment = modifier.getModifierValue();

            adjustPrice(adjustment);

            return false;
        }

        if (wasClicked(stack, confirm))
        {
            if (price > 0.0D)
            {
                added = true;

                shop.addItem(this.stack, price);
                player.closeInventory();
            }
            else
            {
                Message.send(player, Message.SHOP_PRICE_MINIMUM, shop.getName());
            }
        }
        return false;
    }

    @Override
    public void onClose(Player player)
    {
        if (!added && newItem)
        {
            player.getInventory().addItem(stack);
        }
    }

    private void setPrice(double price)
    {
        this.price = price;
    }

    private void adjustPrice(double price)
    {
        if (this.price + price < 0.0D)
        {
            return;
        }

        this.price += price;

        updateLore();
    }

    private void updateLore()
    {
        ItemStack currentPriceStack = currentPrice.getStack();
        ItemStack newStack = UtilItem.createStack(currentPriceStack.getType(), currentPriceStack.getDurability(),
                String.format(currentPriceStack.getItemMeta().getDisplayName().replace(String.valueOf(0.0D), "%s"), price));

        setItem(currentPrice.getSlot(), newStack);
    }

    private MenuButton getClickedButton(ItemStack stack)
    {
        for (MenuButton button : priceModifiers)
        {
            if (wasClicked(stack, button))
            {
                return button;
            }
        }

        return null;
    }

    private void setInitialLore()
    {
        ItemMeta meta = currentPrice.getStack().getItemMeta();
        meta.setDisplayName(String.format(meta.getDisplayName(), price));
        currentPrice.getStack().setItemMeta(meta);
    }
}
