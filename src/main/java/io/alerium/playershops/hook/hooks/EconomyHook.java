package io.alerium.playershops.hook.hooks;

import io.alerium.playershops.PlayerShops;
import io.alerium.playershops.hook.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHook extends Hook {
    public EconomyHook(PlayerShops plugin) {
        super(plugin, "Vault");
    }

    private Economy economy;

    @Override
    public boolean init() {
        if (!super.init())
            return false;

        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (serviceProvider == null) 
            return false;

        economy = serviceProvider.getProvider();
        return economy != null;
    }

    public boolean canAfford(Player player, double amount) {
        return economy.getBalance(player) >= amount;
    }

    public void removeMoney(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public void addMoney(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }
}
