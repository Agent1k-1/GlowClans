package com.agent1k.action;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class CommandAction implements Action {

    @Override
    public void execute(@NotNull Player player, @NotNull String text) {
        String command = text.trim();
        if (command.isEmpty()) return;
        if (command.startsWith("/")) command = command.substring(1);
        Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }
        Bukkit.dispatchCommand(player, command);
    }

    @Override
    public @NotNull ActionType getType() {
        return ActionType.COMMAND;
    }
}
