package io.alerium.playershops.hook.hooks;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.hook.Hook;
import io.alerium.playershops.shop.Shop;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CitizensHook extends Hook {
    public CitizensHook(PlayerShops plugin) {
        super(plugin, "Citizens");
    }

    private NPCRegistry npcRegistry;

    @Override
    public boolean init() {
        if (!super.init())
            return false;

        npcRegistry = CitizensAPI.getNPCRegistry();
        return npcRegistry != null;
    }

    public NPC spawnShopNpc(Player player, Location location) {
        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, player.getName());

        npc.setName(player.getName() + "'s Shop");
        npc.spawn(location);
        npc.setProtected(true);

        setNpcSkin(npc, player.getName());
        return npc;
    }

    public void setNpcSkin(NPC npc, String player) {
        if (!npc.hasTrait(SkinTrait.class))
            npc.addTrait(SkinTrait.class);
        
        npc.getTrait(SkinTrait.class).setSkinName(player, true);
    }

    public NPC getNpcFromShop(Shop shop) {
        return npcRegistry.getByUniqueId(shop.getId());
    }

    public Shop getShop(NPC npc) {
        for (Shop shop : plugin.getShopManager().getShops()) {
            if (shop.getNpc() == npc) 
                return shop;
        }

        return null;
    }
}
