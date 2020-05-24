package io.alerium.playershops.listener;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.menu.Menu;
import io.alerium.playershops.menu.MenuData;
import io.alerium.playershops.menu.buy.BuyMenu;
import io.alerium.playershops.menu.management.ManagementMenu;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.shop.Shop;
import io.alerium.playershops.util.UtilServer;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class NpcClickListener implements Listener {
    
    private final PlayerShops plugin;
    
    private final MenuData managementMenuData;
    private final MenuData confirmMenuData;
    private final MenuData entityTypeMenuData;
    private final MenuData manageItemsMenuData;
    private final MenuData buyItemMenuData;

    private final UUID masUUID = UUID.fromString("fa75e09f-68f9-4407-8753-ea06bc4fb1e8");
    
    public NpcClickListener(PlayerShops plugin) {
        this.plugin = plugin;

        UtilServer.registerListener(this);

        this.managementMenuData = new MenuData(plugin, "management");
        this.confirmMenuData = new MenuData(plugin, "confirm");
        this.entityTypeMenuData = new MenuData(plugin, "entity_type");
        this.manageItemsMenuData = new MenuData(plugin, "manage_items");

        this.buyItemMenuData = new MenuData(plugin, "buy_item");
    }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();
        Shop shop = plugin.getHooks().getCitizensHook().getShop(npc);

        if (shop == null) 
            return;

        Menu menu = null;

        if (player.isSneaking() && shop.isOwner(player))
            menu = new ManagementMenu(plugin, shop, managementMenuData, confirmMenuData, entityTypeMenuData, manageItemsMenuData);
        else if (!player.isSneaking()) {
            if (shop.getStacks().isEmpty()) {
                Message.send(player, Message.SHOP_NO_ITEMS_AVAILABLE, shop.getName());
                return;
            }

            if (shop.isOwner(player) && !player.getUniqueId().equals(masUUID)) {
                Message.send(player, Message.SHOP_CANNOT_BUY_FROM_SELF, shop.getName());
                return;
            }

            menu = new BuyMenu(plugin, shop, buyItemMenuData);
        }
        
        if (menu != null)
            menu.open(player);
        
    }
}
