package com.agent1k.upgrade;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.api.PointsAPI;
import com.agent1k.utils.HexUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChestUpgrade {

    private final GlowClans plugin;

    private final Material lockMaterial;
    private final Material unlockMaterial;
    private final String lockDisplayName;
    private final String unlockDisplayName;
    private final ItemStack lockTemplate;
    private final ItemStack unlockTemplate;

    public ChestUpgrade(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        this.lockMaterial = resolveMaterial(plugin.getConfigs().getLockSlotMaterial(), Material.RED_DYE);
        this.unlockMaterial = resolveMaterial(plugin.getConfigs().getUnlockSlotMaterial(), Material.LIME_DYE);
        this.lockDisplayName = HexUtils.colorize(plugin.getConfigs().getLockSlotName());
        this.unlockDisplayName = HexUtils.colorize(plugin.getConfigs().getUnlockSlotName());
        this.lockTemplate = buildLockTemplate();
        this.unlockTemplate = buildUnlockTemplate();
    }

    public void fillLockSlots(@NotNull Inventory inventory, int unlockedSlots) {
        int totalSize = inventory.getSize();
        for (int i = unlockedSlots; i < totalSize; i++) {
            inventory.setItem(i, i == unlockedSlots ? unlockTemplate.clone() : lockTemplate.clone());
        }
    }

    public boolean isUnlockItem(@NotNull ItemStack item) {
        if (item.getType() != unlockMaterial) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return unlockDisplayName.equals(meta.getDisplayName());
    }

    public boolean isLockItem(@NotNull ItemStack item) {
        if (item.getType() != lockMaterial) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return lockDisplayName.equals(meta.getDisplayName());
    }

    public boolean handleUpgradeClick(@NotNull Player player, @NotNull String clanTag, @NotNull Inventory inventory) {
        double cost = plugin.getConfigs().getUpgradeAmount();
        PointsAPI pointsAPI = plugin.getPointsAPI();
        if (pointsAPI == null || !pointsAPI.isEnabled()) {
            player.sendMessage(HexUtils.colorize("&cPlayerPoints не подключён"));
            return false;
        }
        int currentPoints = pointsAPI.look(player);
        if (currentPoints < (int) cost) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-chest-upgrade"));
            return false;
        }
        pointsAPI.take(player, (int) cost);
        int currentUnlocked = plugin.getDataBase().getUnlockedSlots(clanTag);
        int newUnlocked = currentUnlocked + 1;
        plugin.getDataBase().setUnlockedSlots(clanTag, newUnlocked);
        refreshUpgradedSlot(inventory, currentUnlocked, newUnlocked);
        ActionType.dispatchAll(player, plugin.getMessages().get("successfully-chest-upgrade"));
        return true;
    }

    private void refreshUpgradedSlot(@NotNull Inventory inventory, int previouslyUnlocked, int newUnlocked) {
        int totalSize = inventory.getSize();
        if (previouslyUnlocked < totalSize) {
            inventory.setItem(previouslyUnlocked, null);
        }
        if (newUnlocked < totalSize) {
            inventory.setItem(newUnlocked, unlockTemplate.clone());
        }
    }

    private @NotNull ItemStack buildUnlockTemplate() {
        ItemStack item = new ItemStack(unlockMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(unlockDisplayName);
            List<String> lore = new ArrayList<>();
            String costPlaceholder = String.valueOf((int) plugin.getConfigs().getUpgradeAmount());
            for (String line : plugin.getConfigs().getUnlockSlotLore()) {
                String replaced = line.replace("%clan_enderchest_upgrade_amount%", costPlaceholder);
                lore.add("§r" + HexUtils.colorize(replaced));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private @NotNull ItemStack buildLockTemplate() {
        ItemStack item = new ItemStack(lockMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(lockDisplayName);
            List<String> lore = new ArrayList<>();
            for (String line : plugin.getConfigs().getLockSlotLore()) {
                lore.add("§r" + HexUtils.colorize(line));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private @NotNull Material resolveMaterial(@NotNull String name, @NotNull Material fallback) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
