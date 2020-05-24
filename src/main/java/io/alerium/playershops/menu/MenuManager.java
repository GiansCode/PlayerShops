package io.alerium.playershops.menu;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.util.UtilServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MenuManager implements Listener
{
    private final PlayerShops plugin;

    public MenuManager(PlayerShops plugin)
    {
        this.plugin = plugin;

        UtilServer.registerListener(this);
    }

    private final Map<Player, Menu> menus = new HashMap<>();

    void updateMenu(Player player, Menu menu)
    {
        menus.put(player, menu);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null)
        {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Menu menu = menus.get(player);

        if (menu != null)
        {
            InventoryAction action = event.getAction();

            if (action.name().contains("PICKUP") && inventory.getType() != InventoryType.PLAYER)
            {
                ItemStack clicked = event.getCurrentItem();

                if (clicked != null && clicked.getType() != Material.AIR)
                {
                    boolean allow = menu.onClick(player, clicked, event.getClick(), event.getSlot());
                    event.setCancelled(!allow);

                    return;
                }
            }
            else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && inventory.getType() == InventoryType.PLAYER)
            {
                if (menu instanceof Placeable)
                {
                    ItemStack stack = event.getCurrentItem();

                    boolean allow = ((Placeable) menu).onPlace(player, stack);
                    event.setCancelled(!allow);

                    return;
                }

                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event)
    {
        Player player = (Player) event.getWhoClicked();

        Menu menu = menus.get(player);

        if (menu != null)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();

        Menu menu = menus.remove(player);

        if (menu instanceof CloseListener)
        {
            ((CloseListener) menu).onClose(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        menus.remove(event.getPlayer());
    }
}
