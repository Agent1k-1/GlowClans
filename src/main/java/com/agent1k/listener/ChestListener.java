package com.agent1k.listener;

import com.agent1k.GlowClans;
import com.agent1k.upgrade.UpgradeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChestListener implements Listener {

    private final GlowClans plugin;

    public ChestListener(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Optional<String> chestTagOpt = plugin.getDataBase().getClanByChest(inventory);
        if (!chestTagOpt.isPresent()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Optional<String> playerTagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        if (!playerTagOpt.isPresent() || !chestTagOpt.get().equals(playerTagOpt.get())) {
            event.setCancelled(true);
            return;
        }
        if (plugin.getConfigs().getUpgradeType() == UpgradeType.UPGRADE_CHEST) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null) {
                if (plugin.getChestUpgrade().isLockItem(clicked)) {
                    event.setCancelled(true);
                } else if (plugin.getChestUpgrade().isUnlockItem(clicked)) {
                    event.setCancelled(true);
                    plugin.getChestUpgrade().handleUpgradeClick(player, chestTagOpt.get(), inventory);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        Optional<String> chestTagOpt = plugin.getDataBase().getClanByChest(inventory);
        if (!chestTagOpt.isPresent()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Optional<String> playerTagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        if (!playerTagOpt.isPresent() || !chestTagOpt.get().equals(playerTagOpt.get())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        plugin.getDataBase().getClanByChest(inventory).ifPresent(tag ->
                plugin.getDataBase().saveChest(tag, inventory)
        );
    }
}
