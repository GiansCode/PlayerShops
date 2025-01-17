package io.alerium.playershops.shop;

import com.google.gson.JsonObject;
import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.util.Logs;
import io.alerium.playershops.util.UtilItem;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Shop {
    
    private final PlayerShops plugin;
    
    @Getter private final OfflinePlayer owner;
    @Getter private String name;
    @Getter private final UUID id;
    
    private final Map<ItemStack, Double> items;
    
    @Getter private final NPC npc;
    
    Shop(PlayerShops plugin, Player player, NPC npc, String name) {
        this.plugin = plugin;

        this.owner = player;
        this.name = name;
        this.id = npc.getUniqueId();

        this.items = new HashMap<>();

        this.npc = npc;
    }

    Shop(PlayerShops plugin, JsonObject json) {
        this.plugin = plugin;

        this.owner = Bukkit.getOfflinePlayer(UUID.fromString(json.get("owner").getAsString()));
        this.name = json.get("name").getAsString();
        this.id = UUID.fromString(json.get("id").getAsString());
        this.items = UtilItem.deserializeItems(json.getAsJsonArray("items"));

        this.npc = plugin.getHooks().getCitizensHook().getNpcFromShop(this);

        if (npc == null) 
            Logs.error("Could not find NPC for shop " + id);
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(owner.getUniqueId());
    }

    public void addItem(ItemStack stack, double price) {
        Player player = getPlayer();
        if (player == null)
            return;
        
        if (items.put(stack, price) != null) // Updated
            Message.send(player, Message.SHOP_ITEM_UPDATED, getName());
        else // Added
            Message.send(player, Message.SHOP_ITEM_ADDED, getName());
    }

    public void removeItem(ItemStack stack, boolean bought) {
        assert items.containsKey(stack) : "Shop items does not contain stack";

        items.remove(stack);
        if (bought)
            return;
        
        Player player = getPlayer();
        if (player == null)
            return;
        
        items.remove(stack);
        player.getInventory().addItem(stack);

        Message.send(player, Message.SHOP_ITEM_REMOVED, getName());
    }

    public double getPrice(ItemStack stack) {
        return items.get(stack);
    }

    public void buy(Player player, ItemStack stack, double price) {
        plugin.getHooks().getEconomyHook().removeMoney(player, price);

        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItem(player.getLocation(), stack);
        else 
            player.getInventory().addItem(stack);

        removeItem(stack, true);
        plugin.getHooks().getEconomyHook().addMoney(getOwner(), price);

        Message.send(player, Message.SHOP_ITEM_BOUGHT_SUCCESSFULLY, getName());
    }

    public Set<ItemStack> getStacks() {
        return Collections.unmodifiableSet(items.keySet());
    }

    public Map<ItemStack, Double> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public int getGuiSize() {
        int itemCount = getStacks().size();

        while (itemCount % 9 != 0)
            ++itemCount;

        return itemCount / 9;
    }

    public void remove() {
        Player player = getPlayer();
        if (player == null)
            return;
        
        npc.destroy();
        plugin.getShopManager().removeShop(this);

        if (player.getInventory().firstEmpty() == -1) 
            player.getLocation().getWorld().dropItem(player.getLocation(), plugin.getShopCreateItem());
        else
            player.getInventory().addItem(plugin.getShopCreateItem());

        Message.send(player, Message.SHOP_REMOVED, getName());
    }

    public void rename(String name) {
        Player player = getPlayer();
        if (player == null)
            return;
        
        this.name = name;
        npc.setName(name);

        Message.send(player, Message.SHOP_RENAMED, getName());
    }

    public void setEntityType(EntityType type) {
        Player player = getPlayer();
        if (player == null)
            return;
        
        npc.setBukkitEntityType(type);
        npc.setName(getName());

        Message.send(player, Message.SHOP_ENTITY_TYPE_CHANGED, getName());
    }

    private Player getPlayer() {
        return owner.getPlayer();
    }

    JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("owner", owner.getUniqueId().toString());
        object.addProperty("name", name);
        object.addProperty("id", id.toString());
        object.add("items", UtilItem.serializeItems(items));

        return object;
    }

    public void move(Player player, Location location) {
        npc.teleport(location, TeleportCause.PLUGIN);
        Message.send(player, Message.SHOP_MOVED);
    }
}
