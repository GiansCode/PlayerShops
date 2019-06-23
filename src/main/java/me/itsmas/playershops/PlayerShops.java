package me.itsmas.playershops;

import me.itsmas.playershops.command.ShopsCommand;
import me.itsmas.playershops.hook.HookManager;
import me.itsmas.playershops.listener.NpcClickListener;
import me.itsmas.playershops.listener.ShopPlaceListener;
import me.itsmas.playershops.menu.MenuManager;
import me.itsmas.playershops.message.Message;
import me.itsmas.playershops.shop.ShopManager;
import me.itsmas.playershops.util.Logs;
import me.itsmas.playershops.util.UtilItem;
import me.itsmas.playershops.util.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerShops extends JavaPlugin
{
    private HookManager hookManager; public HookManager getHooks() { return hookManager; }
    private ShopManager shopManager; public ShopManager getShopManager() { return shopManager; }
    private MenuManager menuManager; public MenuManager getMenuManager() { return menuManager; }
    private ShopPlaceListener placeListener; public ShopPlaceListener getPlaceListener() { return placeListener; }

    @Override
    public void onEnable()
    {
        if (!enable())
        {
            Logs.error("Unable to enable PlayerShops");
            Logs.error("Ensure you have all dependencies installed");
        }
    }

    private ItemStack shopCreateItem;

    public ItemStack getShopCreateItem()
    {
        return shopCreateItem.clone();
    }

    @Override
    public void onDisable()
    {
        getShopManager().saveShops();
    }

    private boolean enable()
    {
        initConfig();

        if (!Message.init(this))
        {
            return false;
        }

        if (!initHooks())
        {
            return false;
        }

        shopManager = new ShopManager(this);
        menuManager = new MenuManager(this);

        new NpcClickListener(this);
        placeListener = new ShopPlaceListener(this);

        shopCreateItem = parseShopItem();

        if (shopCreateItem == null)
        {
            return false;
        }

        getCommand("playershops").setExecutor(new ShopsCommand(this));

        return true;
    }

    private ItemStack parseShopItem()
    {
        try
        {
            Material material = Material.valueOf(((String) getConfig("shop_creator_item.material")).toUpperCase());
            int data = getConfig("shop_creator_item.data");
            String name = UtilString.colour(getConfig("shop_creator_item.name"));

            List<String> lore = getConfig("shop_creator_item.lore");
            List<String> colouredLore = lore.stream().map(UtilString::colour).collect(Collectors.toList());

            ItemStack stack = UtilItem.createStack(material, (short) data, name);
            stack = UtilItem.addLore(stack, colouredLore);

            return stack;
        }
        catch (Exception ex)
        {
            Logs.error("Unable to parse shop creator item:");
            ex.printStackTrace();
        }

        return null;
    }

    private void initConfig()
    {
        saveDefaultConfig();
    }

    private boolean initHooks()
    {
        hookManager = new HookManager(this);

        return hookManager.init();
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
