package me.itsmas.playershops.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class UtilString
{
    public static String colour(String input)
    {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String toTitle(String input)
    {
        return WordUtils.capitalizeFully(input);
    }

    private static final char[] allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray();

    public static boolean isAllowedUsername(String name)
    {
        for (char character : name.toCharArray())
        {
            if (!ArrayUtils.contains(allowedChars, character))
            {
                return false;
            }
        }

        return true;
    }
}
