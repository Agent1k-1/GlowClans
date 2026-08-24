package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCMD implements subcommand {

    private static final String PERMISSION = "clan.reload";

    private final GlowClans plugin;

    public ReloadCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission(PERMISSION)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("reload-use"));
            return;
        }
        plugin.reload();
        ActionType.dispatchAll(player, plugin.getMessages().get("plugin-reload"));
    }

    @Override
    public @NotNull String getName() {
        return "reload";
    }
}
