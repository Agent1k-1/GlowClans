package com.agent1k.action;

import com.agent1k.GlowClans;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CloseAction implements Action {

    @Override
    public void execute(@NotNull Player player, @NotNull String text) {
        GlowClans plugin = JavaPlugin.getPlugin(GlowClans.class);
        Bukkit.getScheduler().runTask(plugin, (Runnable) player::closeInventory);
    }

    @Override
    public @NotNull ActionType getType() {
        return ActionType.CLOSE;
    }
}
