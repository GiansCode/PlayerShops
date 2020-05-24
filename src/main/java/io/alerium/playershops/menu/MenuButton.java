package io.alerium.playershops.menu;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class MenuButton {
    
    @Getter private final ItemStack stack;
    @Getter private final int slot;

    @Getter private double modifierValue;

    MenuButton(ItemStack stack, int slot, String key) {
        this.stack = stack;
        this.slot = slot;

        if (key.startsWith("add_")) {
            try {
                modifierValue = Double.parseDouble(key.split("_")[1]);
            } catch (NumberFormatException ignored) {
            }
        } else if (key.startsWith("remove_")) {
            try {
                modifierValue = -Double.parseDouble(key.split("_")[1]);
            } catch (NumberFormatException ignored) {
            }
        }
    }
    
}
