package com.agent1k.action;

import com.agent1k.utils.HexUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageAction implements Action {

    private static final boolean PAPI_ENABLED =
            Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    @Override
    public void execute(@NotNull Player player, @NotNull String text) {
        String parsed = PAPI_ENABLED ? PlaceholderAPI.setPlaceholders(player, text) : text;
        player.sendMessage(HexUtils.colorize(parsed));
    }

    @Override
    public @NotNull ActionType getType() {
        return ActionType.MESSAGE;
    }
}
