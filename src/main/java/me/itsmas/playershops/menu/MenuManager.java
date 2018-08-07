package me.itsmas.playershops.menu;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.util.UtilServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
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
        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER)
        {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Menu menu = menus.get(player);

        if (menu != null)
        {
            InventoryAction action = event.getAction();

            if (action.name().contains("PICKUP"))
            {
                ItemStack clicked = event.getCurrentItem();

                if (clicked != null && clicked.getType() != Material.AIR)
                {
                    boolean allow = menu.onClick(player, clicked, event.getClick(), event.getSlot());
                    event.setCancelled(!allow);
                }
            }
            else if (action == InventoryAction.SWAP_WITH_CURSOR || action == InventoryAction.MOVE_TO_OTHER_INVENTORY)
            {
                event.setCancelled(true);
            }
            else if (action.name().contains("PLACE"))
            {
                ItemStack dragged = event.getCursor();

                if (menu instanceof Placeable)
                {
                    boolean allow = ((Placeable) menu).onPlace(player, dragged);
                    event.setCancelled(!allow);

                    return;
                }

                event.setCancelled(true);
            }
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

        if (menu != null && menu instanceof CloseListener)
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
