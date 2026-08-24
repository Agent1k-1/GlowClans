package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuCMD implements subcommand {

    private final GlowClans plugin;

    public MenuCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (!plugin.getDataBase().hasClan(player.getUniqueId())) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-no-clan"));
            return;
        }
        plugin.getClanGUI().open(player);
    }

    @Override
    public @NotNull String getName() {
        return "menu";
    }
}
