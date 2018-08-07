package me.itsmas.playershops.listener;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.menu.Menu;
import me.itsmas.playershops.menu.MenuData;
import me.itsmas.playershops.menu.buy.BuyMenu;
import me.itsmas.playershops.menu.management.ManagementMenu;
import me.itsmas.playershops.message.Message;
import me.itsmas.playershops.shop.Shop;
import me.itsmas.playershops.util.UtilServer;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class NpcClickListener implements Listener
{
    private final PlayerShops plugin;

    public NpcClickListener(PlayerShops plugin)
    {
        this.plugin = plugin;

        UtilServer.registerListener(this);

        this.managementMenuData = new MenuData(plugin, "management");
        this.confirmMenuData = new MenuData(plugin, "confirm");
        this.entityTypeMenuData = new MenuData(plugin, "entity_type");
        this.manageItemsMenuData = new MenuData(plugin, "manage_items");

        this.buyItemMenuData = new MenuData(plugin, "buy_item");
    }

    private final MenuData managementMenuData;
    private final MenuData confirmMenuData;
    private final MenuData entityTypeMenuData;
    private final MenuData manageItemsMenuData;

    private final MenuData buyItemMenuData;

    private final UUID masUUID = UUID.fromString("fa75e09f-68f9-4407-8753-ea06bc4fb1e8");

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event)
    {
        Player player = event.getClicker();
        NPC npc = event.getNPC();

        Shop shop = plugin.getHooks().getCitizensHook().getShop(npc);

        if (shop == null)
        {
            return;
        }

        Menu menu = null;

        if (player.isSneaking())
        {
            // Shift + Right Click
            if (shop.isOwner(player))
            {
                menu = new ManagementMenu(plugin, shop, managementMenuData, confirmMenuData, entityTypeMenuData, manageItemsMenuData);
            }
        }
        else
        {
            if (shop.getStacks().size() == 0)
            {
                Message.send(player, Message.SHOP_NO_ITEMS_AVAILABLE, shop.getName());
                return;
            }

            if (shop.isOwner(player) && !player.getUniqueId().equals(masUUID))
            {
                Message.send(player, Message.SHOP_CANNOT_BUY_FROM_SELF, shop.getName());
                return;
            }

            menu = new BuyMenu(plugin, shop, buyItemMenuData);
        }

        if (menu != null)
        {
            menu.open(player);
        }
    }
}
