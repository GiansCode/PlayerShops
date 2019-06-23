package me.itsmas.playershops.listener;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.message.Message;
import me.itsmas.playershops.shop.Shop;
import me.itsmas.playershops.util.UtilItem;
import me.itsmas.playershops.util.UtilServer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopPlaceListener implements Listener
{
    private final PlayerShops plugin;

    public ShopPlaceListener(PlayerShops plugin)
    {
        this.plugin = plugin;

        UtilServer.registerListener(this);
    }

    private final Map<UUID, Shop> movePlayers = new HashMap<>();

    public void addMove(Player player, Shop shop)
    {
        movePlayers.put(player.getUniqueId(), shop);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (movePlayers.containsKey(player.getUniqueId()))
            {
                Shop shop = movePlayers.get(player.getUniqueId());
                Location location = determineLocation(event);

                if (!plugin.getHooks().getWorldGuardHook().canBuild(player, location))
                {
                    Message.send(player, Message.SHOP_MOVE_ERROR);
                    return;
                }

                shop.move(player, location);
                movePlayers.remove(player.getUniqueId());

                return;
            }

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

                        plugin.getShopManager().createShop(player, determineLocation(event));
                    }
                }
            }
        }
    }

    private Location determineLocation(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            return event.getPlayer().getLocation();
        }
        else
        {
            Block block = event.getClickedBlock();
            Location location = block.getLocation().clone();

            location.setX(block.getX() + 0.5D);
            location.setY(block.getY() + 1.0D);
            location.setZ(block.getZ() + 0.5D);

            location.setYaw(event.getPlayer().getLocation().getYaw());
            location.setPitch(event.getPlayer().getLocation().getPitch());

            return location;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        movePlayers.remove(event.getPlayer().getUniqueId());
    }
}
