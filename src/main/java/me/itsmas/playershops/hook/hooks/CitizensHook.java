package me.itsmas.playershops.hook.hooks;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.hook.Hook;
import me.itsmas.playershops.shop.Shop;
import me.itsmas.playershops.util.UtilServer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CitizensHook extends Hook
{
    public CitizensHook(PlayerShops plugin)
    {
        super(plugin, "Citizens");
    }

    private NPCRegistry npcRegistry;

    @Override
    public boolean init()
    {
        if (!super.init())
        {
            return false;
        }

        npcRegistry = CitizensAPI.getNPCRegistry();
        return npcRegistry != null;
    }

    public NPC spawnShopNpc(Player player, Location location)
    {
        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, player.getName());

        npc.setName(player.getName() + "'s Shop");
        npc.spawn(location);

        npc.setProtected(true);

        setNpcSkin(npc, player.getName());

        return npc;
    }

    public void setNpcSkin(NPC npc, String player)
    {
        // Dirty way since there doesn't seem to be an API function for setting skin
        UtilServer.dispatchCommand("npc select " + npc.getId());
        UtilServer.dispatchCommand("npc skin " + player);
    }

    public NPC getNpcFromShop(Shop shop)
    {
        return npcRegistry.getByUniqueId(shop.getId());
    }

    public Shop getShop(NPC npc)
    {
        for (Shop shop : plugin.getShopManager().getShops())
        {
            if (shop.getNpc() == npc)
            {
                return shop;
            }
        }

        return null;
    }
}
