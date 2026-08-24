package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCMD implements subcommand {

    private final GlowClans plugin;

    public HelpCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        String key = plugin.getDataBase().hasClan(player.getUniqueId()) ? "clan-help" : "no-clan-help";
        ActionType.dispatchAll(player, plugin.getMessages().get(key));
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }
}
