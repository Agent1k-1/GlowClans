package com.agent1k.config;

import com.agent1k.GlowClans;
import com.agent1k.upgrade.UpgradeType;
import com.agent1k.utils.HexUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Configs {

    private final GlowClans plugin;
    private double createCost;
    private double createHomeCost;
    private double maxWithdraw;
    private boolean clanTagLimitEnabled;
    private int clanTagMaxLength;
    private int chestSize;
    private String chestInventoryName;
    private UpgradeType upgradeType;
    private double upgradeAmount;
    private int defaultUnlock;
    private String unlockSlotMaterial;
    private String unlockSlotName;
    private List<String> unlockSlotLore;
    private String lockSlotMaterial;
    private String lockSlotName;
    private List<String> lockSlotLore;

    public Configs(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        createCost = plugin.getConfig().getDouble("settings.create");
        createHomeCost = plugin.getConfig().getDouble("settings.create-home");
        maxWithdraw = plugin.getConfig().getDouble("settings.max-withdraw", 25000.0);
        clanTagLimitEnabled = plugin.getConfig().getBoolean("settings.clan-tag.enabled", false);
        clanTagMaxLength = plugin.getConfig().getInt("settings.clan-tag.tag", 15);
        chestSize = plugin.getConfig().getInt("clan-chest.size");
        chestInventoryName = HexUtils.colorize(plugin.getConfig().getString("clan-chest.inventory-name"));
        upgradeType = UpgradeType.fromString(plugin.getConfig().getString("upgrade-settings.type-upgrade.type"));
        upgradeAmount = plugin.getConfig().getDouble("upgrade-settings.upgrade-amount");
        defaultUnlock = plugin.getConfig().getInt("upgrade-settings.default_unlock");
        unlockSlotMaterial = plugin.getConfig().getString("upgrade-settings.unlock_slot.material");
        unlockSlotName = plugin.getConfig().getString("upgrade-settings.unlock_slot.name");
        unlockSlotLore = plugin.getConfig().getStringList("upgrade-settings.unlock_slot.lore");
        lockSlotMaterial = plugin.getConfig().getString("upgrade-settings.lock-slot.material");
        lockSlotName = plugin.getConfig().getString("upgrade-settings.lock-slot.name");
        lockSlotLore = plugin.getConfig().getStringList("upgrade-settings.lock-slot.lore");
    }

    public double getCreateCost() { return createCost; }
    public double getCreateHomeCost() { return createHomeCost; }
    public double getMaxWithdraw() { return maxWithdraw; }
    public boolean isClanTagLimitEnabled() { return clanTagLimitEnabled; }
    public int getClanTagMaxLength() { return clanTagMaxLength; }
    public int getChestSize() { return chestSize; }
    public @NotNull String getChestInventoryName() { return chestInventoryName; }
    public @NotNull UpgradeType getUpgradeType() { return upgradeType; }
    public double getUpgradeAmount() { return upgradeAmount; }
    public int getDefaultUnlock() { return defaultUnlock; }
    public @NotNull String getUnlockSlotMaterial() { return unlockSlotMaterial; }
    public @NotNull String getUnlockSlotName() { return unlockSlotName; }
    public @NotNull List<String> getUnlockSlotLore() { return unlockSlotLore; }
    public @NotNull String getLockSlotMaterial() { return lockSlotMaterial; }
    public @NotNull String getLockSlotName() { return lockSlotName; }
    public @NotNull List<String> getLockSlotLore() { return lockSlotLore; }
}
