package io.alerium.playershops.menu;

import org.bukkit.inventory.ItemStack;

public class MenuButton
{
    private final ItemStack stack;
    private final int slot;

    MenuButton(ItemStack stack, int slot, String key)
    {
        this.stack = stack;
        this.slot = slot;

        if (key.startsWith("add_"))
        {
            try
            {
                modifierValue = Double.parseDouble(key.split("_")[1]);
            }
            catch (NumberFormatException ignored){}
        }
        else if (key.startsWith("remove_"))
        {
            try
            {
                modifierValue = -Double.parseDouble(key.split("_")[1]);
            }
            catch (NumberFormatException ignored){}
        }
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public int getSlot()
    {
        return slot;
    }

    private double modifierValue;

    public double getModifierValue()
    {
        return modifierValue;
    }
}
