package me.itsmas.playershops.message;

import me.itsmas.playershops.PlayerShops;
import me.itsmas.playershops.util.Logs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Message
{
    SHOP_LOCATION_ERROR,
    SHOP_AMOUNT_ERROR,
    SHOP_CREATED,
    SHOP_REMOVED,
    SHOP_NAME_IDENTICAL,
    CHANGE_EXISTING_SHOP_NAME,
    SHOP_RENAMED,
    SHOP_NAME_EXISTS,
    SHOP_NAME_LENGTH,
    SHOP_ENTITY_TYPE_EXPENSIVE,
    SHOP_ENTITY_TYPE_CHANGED,
    SHOP_INVALID_SKIN_NAME,
    SHOP_ITEM_ADDED,
    SHOP_ITEM_UPDATED,
    SHOP_ITEM_LIMIT,
    SHOP_PRICE_MINIMUM,
    SHOP_ITEM_REMOVED,
    SHOP_NO_ITEMS_TO_REMOVE,
    SHOP_NO_ITEMS_TO_MODIFY,
    SHOP_CANNOT_BUY_FROM_SELF,
    SHOP_NO_ITEMS_AVAILABLE,
    SHOP_CANNOT_AFFORD_BUY,
    SHOP_ITEM_BOUGHT_SUCCESSFULLY,
    COMMAND_USAGE_GENERAL,
    COMMAND_USAGE_GIVE,
    COMMAND_INVALID_PLAYER,
    COMMAND_INVALID_NUMBER,
    COMMAND_GIVE_GIVEN,
    COMMAND_GIVE_RECEIVED,
    COMMAND_NO_PERMISSION,
    COMMAND_ONLY_PLAYERS,
    COMMAND_SHOPS_NON_OWNED,
    COMMAND_SHOPS_FORMAT,
    COMMAND_SHOPS_TELEPORTED,
    COMMAND_SHOPS_SEPARATOR,
    SHOP_MOVE,
    SHOP_MOVED,
    SHOP_MOVE_ERROR;

    private String msg;

    private void setValue(String msg)
    {
        assert this.msg == null : "Message is already set";

        this.msg = msg;
    }

    public String value()
    {
        return msg;
    }

    public static void send(CommandSender player, Message message, Object... params)
    {
        if (!message.value().isEmpty())
        {
            player.sendMessage(String.format(message.value(), params));
        }
    }

    public static boolean init(PlayerShops plugin)
    {
        String messagesPath = "messages";

        for (Message message : values())
        {
            String value = plugin.getConfig(messagesPath + "." + message.name().toLowerCase());

            if (value == null)
            {
                Logs.error("No value found for message " + message);
                return false;
            }

            message.setValue(ChatColor.translateAlternateColorCodes('&', value));
        }

        return true;
    }
}
