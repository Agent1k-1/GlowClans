package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import com.agent1k.upgrade.UpgradeType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChestCMD implements subcommand {

    private final GlowClans plugin;

    public ChestCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        Optional<String> tagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        if (tagOpt.isEmpty()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-no-clan"));
            return;
        }
        String tag = tagOpt.get();
        plugin.getDataBase().loadChest(tag).thenAccept(inventory ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (plugin.getConfigs().getUpgradeType() == UpgradeType.UPGRADE_CHEST) {
                        int unlocked = plugin.getDataBase().getUnlockedSlots(tag);
                        plugin.getChestUpgrade().fillLockSlots(inventory, unlocked);
                    }
                    player.openInventory(inventory);
                })
        );
    }

    @Override
    public @NotNull String getName() {
        return "chest";
    }
}
