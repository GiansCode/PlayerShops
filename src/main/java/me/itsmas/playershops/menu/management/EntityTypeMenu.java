package me.itsmas.playershops.menu.management;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.menu.Menu;
import me.itsmas.playershops.menu.MenuButton;
import me.itsmas.playershops.menu.MenuData;
import me.itsmas.playershops.message.Message;
import me.itsmas.playershops.shop.Shop;
import me.itsmas.playershops.util.Logs;
import me.itsmas.playershops.util.UtilItem;
import me.itsmas.playershops.util.UtilPermission;
import me.itsmas.playershops.util.UtilString;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityTypeMenu extends Menu
{
    EntityTypeMenu(PlayerShops plugin, Shop shop, MenuData menuData)
    {
        super(plugin, shop, menuData);

        custom = menuData.getButton("custom");
        customPrice = (long) (int) plugin.getConfig("entity_prices.custom", 10_000);

        priceFormat = UtilString.colour(plugin.getConfig("price_format", "&6Price: &e$%s")).trim();
    }

    private final MenuButton custom;
    private final double customPrice;

    @Override
    public boolean onClick(Player player, ItemStack stack, ClickType click, int slot)
    {
        if (wasClicked(stack, custom))
        {
            if (!plugin.getHooks().getEconomyHook().canAfford(player, customPrice))
            {
                Message.send(player, Message.SHOP_ENTITY_TYPE_EXPENSIVE, shop.getName());
                return false;
            }

            new AnvilGUI(plugin, player, "Skin Here", (p, reply) ->
            {
                if (reply.length() < 3 || reply.length() > 16 || !UtilString.isAllowedUsername(reply))
                {
                    Message.send(player, Message.SHOP_INVALID_SKIN_NAME, shop.getName());
                    return null;
                }

                shop.setEntityType(EntityType.PLAYER);
                plugin.getHooks().getCitizensHook().setNpcSkin(shop.getNpc(), reply);

                plugin.getHooks().getEconomyHook().removeMoney(player, customPrice);

                return null;
            });

            return false;
        }

        String itemName = ChatColor.stripColor(UtilItem.getItemName(stack));
        String normalized = itemName.toUpperCase().replace(" ", "_").trim();

        EntityType type;

        try
        {
            type = EntityType.valueOf(normalized);
        }
        catch (IllegalArgumentException ex)
        {
            // Shouldn't happen
            return false;
        }

        double price = prices.get(type);

        if (!plugin.getHooks().getEconomyHook().canAfford(player, price))
        {
            Message.send(player, Message.SHOP_ENTITY_TYPE_EXPENSIVE, shop.getName());
        }
        else
        {
            shop.setEntityType(type);

            if (type == EntityType.PLAYER)
            {
                plugin.getHooks().getCitizensHook().setNpcSkin(shop.getNpc(), player.getName());
            }

            plugin.getHooks().getEconomyHook().removeMoney(player, price);
        }

        player.closeInventory();
        return false;
    }

    @Override
    public void open(Player player)
    {
        addSpawnEggs(player);

        super.open(player);
    }

    private void addSpawnEggs(Player player)
    {
        Set<EntityType> entityTypes = getEntityTypes(player);

        int index = -1;

        for (EntityType type : entityTypes)
        {
            Material material = Material.valueOf(type.name() + "_SPAWN_EGG");

            ItemStack stack = UtilItem.createStack(material, type.getTypeId(), ChatColor.RESET + UtilString.toTitle(type.name()));
            stack = UtilItem.addLore(stack, formatPrice(prices.get(type)));

            setItem(++index, stack);
        }

        if (UtilPermission.hasPermission(player, "playershops.entity_type.custom"))
        {
            setItem(++index, UtilItem.addLore(custom.getStack(), formatPrice(customPrice)));
        }
    }

    private Set<EntityType> getEntityTypes(Player player)
    {
        Set<EntityType> entityTypes = new HashSet<>();

        for (PermissionAttachmentInfo permission : player.getEffectivePermissions())
        {
            String permissionName = permission.getPermission();

            if (permissionName.startsWith("playershops.entity_type.") && permissionName.split("\\.").length == 3)
            {
                String rawType = permissionName.split("\\.")[2];

                try
                {
                    EntityType type = EntityType.valueOf(rawType.toUpperCase());

                    if (type == shop.getNpc().getEntity().getType())
                    {
                        continue;
                    }

                    prices.put(type, getPrice(type));

                    entityTypes.add(type);
                }
                catch (IllegalArgumentException ex)
                {
                    Logs.error("Invalid entity type in config: " + rawType.toUpperCase());
                }
            }
        }

        return entityTypes;
    }

    private final String priceFormat;

    private final Map<EntityType, Double> prices = new HashMap<>();

    private String formatPrice(double amount)
    {
        return String.format(priceFormat, amount);
    }

    private double getPrice(EntityType type)
    {
        int amount = plugin.getConfig("entity_prices." + type.name().toLowerCase(), 500);

        return (double) amount;
    }
}
