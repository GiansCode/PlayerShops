package io.alerium.playershops;

import io.alerium.playershops.command.ShopsCommand;
import io.alerium.playershops.hook.HookManager;
import io.alerium.playershops.listener.NpcClickListener;
import io.alerium.playershops.listener.ShopPlaceListener;
import io.alerium.playershops.menu.MenuManager;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.shop.ShopManager;
import io.alerium.playershops.util.Logs;
import io.alerium.playershops.util.UtilItem;
import io.alerium.playershops.util.UtilString;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerShops extends JavaPlugin {
    
    @Getter private HookManager hooks;
    @Getter private ShopManager shopManager;
    @Getter private MenuManager menuManager;
    @Getter private ShopPlaceListener placeListener;
    
    @Getter private ItemStack shopCreateItem;
    
    @Override
    public void onEnable() {
        if (!enable()) {
            Logs.error("Unable to enable PlayerShops");
            Logs.error("Ensure you have all dependencies installed");
        }
    }

    @Override
    public void onDisable()
    {
        getShopManager().saveShops();
    }

    private boolean enable() {
        initConfig();

        if (!Message.init(this)) {
            return false;
        }

        if (!initHooks()) {
            return false;
        }

        shopManager = new ShopManager(this);
        menuManager = new MenuManager(this);

        new NpcClickListener(this);
        placeListener = new ShopPlaceListener(this);

        shopCreateItem = parseShopItem();

        if (shopCreateItem == null) {
            return false;
        }

        getCommand("playershops").setExecutor(new ShopsCommand(this));
        return true;
    }

    private ItemStack parseShopItem() {
        try {
            Material material = Material.valueOf(((String) getConfig("shop_creator_item.material")).toUpperCase());
            int data = getConfig("shop_creator_item.data");
            String name = UtilString.colour(getConfig("shop_creator_item.name"));

            List<String> lore = getConfig("shop_creator_item.lore");
            List<String> colouredLore = lore.stream().map(UtilString::colour).collect(Collectors.toList());

            ItemStack stack = UtilItem.createStack(material, (short) data, name);
            stack = UtilItem.addLore(stack, colouredLore);

            return stack;
        } catch (Exception ex) {
            Logs.error("Unable to parse shop creator item:");
            ex.printStackTrace();
        }

        return null;
    }

    private void initConfig() {
        saveDefaultConfig();
    }

    private boolean initHooks() {
        hooks = new HookManager(this);
        return hooks.init();
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String path)
    {
        return (T) getConfig().get(path);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String path, Object defaultValue)
    {
        return (T) getConfig().get(path, defaultValue);
    }
}
