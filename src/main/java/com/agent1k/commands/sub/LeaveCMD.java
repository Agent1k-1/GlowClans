package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LeaveCMD implements subcommand {

    private final GlowClans plugin;

    public LeaveCMD(@NotNull GlowClans plugin) {
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
        if (plugin.getDataBase().isOwner(player.getUniqueId(), tag)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-owner-leave"));
            return;
        }
        plugin.getDataBase().removeMember(player.getUniqueId()).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        ActionType.dispatchAll(player, plugin.getMessages().get("successfully-leave"))
                )
        );
    }

    @Override
    public @NotNull String getName() {
        return "leave";
    }
}
