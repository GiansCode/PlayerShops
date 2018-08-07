package me.itsmas.playershops.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilItem
{
    public static ItemStack createStack(Material material, short data, String name)
    {
        ItemStack stack = new ItemStack(material);

        stack.setDurability(data);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(UtilString.colour(name));

        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack addLore(ItemStack stack, String... lore)
    {
        return addLore(stack, Arrays.asList(lore));
    }

    public static ItemStack addLore(ItemStack stack, List<String> lore)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);

        stack.setItemMeta(meta);
        return stack;
    }

    public static String getItemName(ItemStack stack)
    {
        if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName())
        {
            return stack.getItemMeta().getDisplayName();
        }

        return stack.getType().name();
    }

    private static final Gson gson = new GsonBuilder().create();

    public static JsonArray serializeItems(Map<ItemStack, Double> stacks)
    {
        JsonArray array = new JsonArray();

        for (Map.Entry<ItemStack, Double> entry : stacks.entrySet())
        {
            ItemStack stack = entry.getKey();
            double price = entry.getValue();

            JsonObject stackObj = new JsonObject();

            stackObj.addProperty("price", price);
            stackObj.add("data", gson.toJsonTree(stack.serialize()));

            array.add(stackObj);
        }

        return array;
    }

    public static Map<ItemStack, Double> deserializeItems(JsonArray array)
    {
        Map<ItemStack, Double> stacks = new HashMap<>();

        for (JsonElement element : array)
        {
            JsonObject object = element.getAsJsonObject();

            double price = object.get("price").getAsDouble();
            Map<String, Object> fields = getMap(object.get("data").getAsJsonObject());

            ItemStack stack = ItemStack.deserialize(fields);

            stacks.put(stack, price);
        }

        return stacks;
    }

    private static final Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

    private static Map<String, Object> getMap(JsonObject object)
    {
        return gson.fromJson(object, mapType);
    }
}
