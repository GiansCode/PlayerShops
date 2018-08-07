package me.itsmas.playershops.menu;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.util.UtilItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class MenuData
{
    private final PlayerShops plugin;

    private final String menuName;

    private String name;
    private final int size;

    public MenuData(PlayerShops plugin, String menuName)
    {
        this.plugin = plugin;

        this.menuName = menuName;

        this.name = plugin.getConfig("menus." + menuName + ".name", menuName);
        this.size = plugin.getConfig("menus." + menuName + ".size", 3);
    }

    public String getName()
    {
        return name;
    }

    public MenuData formatName(Object... params)
    {
        name = String.format(name, params);

        return this;
    }

    public int getSize()
    {
        return size;
    }

    private int defaultSlot = -1;

    public MenuButton getButton(String name)
    {
        String base = "menus." + menuName + ".buttons." + name + ".";

        String rawMaterial = plugin.getConfig(base + "material", "STONE");
        Material material;

        try
        {
            material = Material.valueOf(rawMaterial.toUpperCase());
        }
        catch (IllegalArgumentException ex)
        {
            material = Material.STONE;
        }

        int data = plugin.getConfig(base + "data", 0);
        int slot = plugin.getConfig(base + "slot", ++defaultSlot);
        String displayName = plugin.getConfig(base + "name", name);

        ItemStack stack = UtilItem.createStack(material, (byte) data, displayName);

        return new MenuButton(stack, slot, name);
    }

    public Set<MenuButton> getButtons(String... startPhrases)
    {
        Set<MenuButton> buttons = new HashSet<>();

        for (String id : plugin.getConfig().getConfigurationSection("menus." + menuName + ".buttons").getKeys(false))
        {
            for (String phrase : startPhrases)
            {
                if (id.startsWith(phrase))
                {
                    buttons.add(getButton(id));
                }
            }
        }

        return buttons;
    }
}
