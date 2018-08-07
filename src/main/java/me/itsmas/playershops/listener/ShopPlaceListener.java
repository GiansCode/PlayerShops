package me.itsmas.playershops.listener;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.util.UtilItem;
import me.itsmas.playershops.util.UtilServer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopPlaceListener implements Listener
{
    private final PlayerShops plugin;

    public ShopPlaceListener(PlayerShops plugin)
    {
        this.plugin = plugin;

        UtilServer.registerListener(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Location location = player.getLocation();
            ItemStack stack = event.getItem();

            if (stack != null)
            {
                if (stack.getType() == plugin.getShopCreateItem().getType() && UtilItem.getItemName(stack).equalsIgnoreCase(UtilItem.getItemName(plugin.getShopCreateItem())))
                {
                    if (plugin.getShopManager().canCreateShop(player, location))
                    {
                        if (stack.getAmount() == 1)
                        {
                            player.getInventory().remove(stack);
                        }
                        else
                        {
                            stack.setAmount(stack.getAmount() - 1);
                        }

                        plugin.getShopManager().createShop(player, location);
                    }
                }
            }
        }
    }
}
