package com.agent1k.depend;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultAPI {

    private Economy economy;

    public boolean setup() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public boolean has(@NotNull Player player, double amount) {
        return economy.has(player, amount);
    }

    public void withdraw(@NotNull Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public void deposit(@NotNull Player player, double amount) {
        economy.depositPlayer(player, amount);
    }
}
