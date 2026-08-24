package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import com.agent1k.data.ClanHome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HomeCMD implements subcommand {

    private final GlowClans plugin;

    public HomeCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        Optional<String> tagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        if (tagOpt.isEmpty()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-no-clan"));
            return;
        }
        Optional<ClanHome> homeOpt = plugin.getDataBase().getHome(tagOpt.get());
        if (homeOpt.isEmpty()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-sethome"));
            return;
        }
        player.teleport(homeOpt.get().toLocation());
        ActionType.dispatchAll(player, plugin.getMessages().get("teleport-home"));
    }

    @Override
    public @NotNull String getName() {
        return "home";
    }
}
