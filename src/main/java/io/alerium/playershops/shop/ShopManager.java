package io.alerium.playershops.shop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.util.Logs;
import io.alerium.playershops.util.UtilJson;
import io.alerium.playershops.util.UtilPermission;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ShopManager implements Listener {
    
    private final PlayerShops plugin;
    
    private final Set<Shop> shops = new HashSet<>();
    private final String shopsFile = "shops.json";

    public ShopManager(PlayerShops plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Set<Shop> getShops() {
        return Collections.unmodifiableSet(shops);
    }

    public void createShop(Player player, Location location) {
        assert canCreateShop(player, location) : "Player cannot create shop at location";

        String name = player.getName() + "'s Shop";

        if (name.length() > 16) 
            name = player.getName();

        NPC shopNpc = plugin.getHooks().getCitizensHook().spawnShopNpc(player, location);
        Shop shop = new Shop(plugin, player, shopNpc, name);

        shops.add(shop);
        Message.send(player, Message.SHOP_CREATED);
    }

    void removeShop(Shop shop) {
        shops.remove(shop);
    }

    public boolean canNameShop(Player player, String name) {
        for (Shop shop : getOwnedShops(player)) {
            if (shop.getName().equals(name))
                return false;
        }

        return true;
    }

    public boolean canCreateShop(Player player, Location location) {
        if (!plugin.getHooks().getWorldGuardHook().canBuild(player, location)) {
            Message.send(player, Message.SHOP_LOCATION_ERROR);
            return false;
        }

        if (getOwnedShops(player).size() == getMaxShops(player)) {
            Message.send(player, Message.SHOP_AMOUNT_ERROR);
            return false;
        }

        if (!canNameShop(player, player.getName() + "'s Shop")) {
            Message.send(player, Message.CHANGE_EXISTING_SHOP_NAME);
            return false;
        }

        return true;
    }

    private void loadShops() {
        File shopsFile = getShopsFile();

        try (FileReader reader = new FileReader(shopsFile)) {
            JsonObject object = UtilJson.parse(reader);
            JsonArray shops = object.getAsJsonArray("shops");

            for (JsonElement element : shops) {
                JsonObject shopObject = element.getAsJsonObject();
                this.shops.add(new Shop(plugin, shopObject));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveShops() {
        JsonObject shopsObject = new JsonObject();

        JsonArray shopArray = new JsonArray();
        shops.forEach(shop -> shopArray.add(shop.serialize()));

        shopsObject.add("shops", shopArray);

        File shopsFile = getShopsFile();

        try {
            FileWriter writer = new FileWriter(shopsFile);

            writer.append(UtilJson.toPretty(shopsObject));

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logs.error("Unable to save shops file");
            ex.printStackTrace();
        }
    }

    private File getShopsFile() {
        File file = new File(plugin.getDataFolder(), shopsFile);
        if (file.exists())
            return file;
        
        try {
            file.createNewFile();

            writeBlankArray(file);
        } catch (IOException ex) {
            Logs.error("Unable to create shops file");
        }

        return file;
    }

    private void writeBlankArray(File file) throws IOException {
        FileWriter writer = new FileWriter(file);

        JsonObject object = new JsonObject();
        object.add("shops", new JsonArray());

        writer.append(UtilJson.toPretty(object));

        writer.flush();
        writer.close();
    }

    public Set<Shop> getOwnedShops(Player player) {
        return shops.stream().filter(shop -> shop.isOwner(player)).collect(Collectors.toSet());
    }

    private int getMaxShops(Player player) {
        int highest = 0;

        if (UtilPermission.hasPermission(player, null))
            return Integer.MAX_VALUE;

        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String permissionName = permission.getPermission();

            if (permissionName.matches("playershops.limit.\\d+")) {
                int limit = Integer.parseInt(permissionName.split("\\.")[2]);
                highest = Math.max(highest, limit);
            }
        }

        return highest;
    }

    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        loadShops();
    }
}
