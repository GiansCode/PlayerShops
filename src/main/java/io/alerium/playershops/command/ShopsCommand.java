package io.alerium.playershops.command;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.message.Message;
import io.alerium.playershops.shop.Shop;
import io.alerium.playershops.util.UtilPermission;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopsCommand implements CommandExecutor {
    private final PlayerShops plugin;

    public ShopsCommand(PlayerShops plugin) {
        this.plugin = plugin;

        listCommand = Message.COMMAND_SHOPS_FORMAT.value();
    }

    private final String listCommand;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Message.send(sender, Message.COMMAND_USAGE_GENERAL);
            return true;
        }

        switch (args[0].toUpperCase()) {
            case "GIVE":
                giveCommand(sender, args);
                break;

            case "LIST":
                listCommand(sender);
                break;

            case "TP":
                tpCommand(sender, args);
                break;

            default:
                Message.send(sender, Message.COMMAND_USAGE_GENERAL);
        }

        return true;
    }

    private void giveCommand(CommandSender sender, String[] args) {
        if (!UtilPermission.hasPermission((Player) sender, "playershops.command.give")) {
            Message.send(sender, Message.COMMAND_NO_PERMISSION);
            return;
        }

        if (args.length != 3) {
            Message.send(sender, Message.COMMAND_USAGE_GIVE);
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);

        if (player == null) {
            Message.send(sender, Message.COMMAND_INVALID_PLAYER);
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            Message.send(sender, Message.COMMAND_INVALID_NUMBER);
            return;
        }

        ItemStack stack = plugin.getShopCreateItem();
        stack.setAmount(amount);

        player.getInventory().addItem(stack);

        Message.send(sender, Message.COMMAND_GIVE_GIVEN, player.getName(), amount);
        Message.send(player, Message.COMMAND_GIVE_RECEIVED, amount, player.getName());
    }

    private void listCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Message.send(sender, Message.COMMAND_ONLY_PLAYERS);
            return;
        }

        Player player = (Player) sender;
        List<Shop> owned = new ArrayList<>(plugin.getShopManager().getOwnedShops(player));

        if (owned.size() == 0) {
            Message.send(player, Message.COMMAND_SHOPS_NON_OWNED);
            return;
        }

        List<BaseComponent> components = new ArrayList<>();
        components.add(new ComponentBuilder(String.format(listCommand, owned.size())).create()[0]);

        for (Shop shop : owned) {
            String separator = shop == owned.get(owned.size() - 1) ? "" : Message.COMMAND_SHOPS_SEPARATOR.value();

            ComponentBuilder builder = new ComponentBuilder(shop.getName() + separator).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shops tp " + shop.getId()));
            components.add(builder.create()[0]);
        }

        player.spigot().sendMessage(components.toArray(new BaseComponent[0]));
    }

    private void tpCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID id = UUID.fromString(args[1]);
        Shop target = plugin.getShopManager().getOwnedShops(player).stream().filter(shop -> shop.getId().equals(id)).findFirst().orElse(null);
        if (target == null)
            return;

        player.teleport(target.getNpc().getEntity());
        Message.send(player, Message.COMMAND_SHOPS_TELEPORTED, target.getName());
    }

}
