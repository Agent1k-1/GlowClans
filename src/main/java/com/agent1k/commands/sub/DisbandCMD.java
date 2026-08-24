package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DisbandCMD implements subcommand {

    private final GlowClans plugin;

    public DisbandCMD(@NotNull GlowClans plugin) {
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
        if (!plugin.getDataBase().isOwner(player.getUniqueId(), tag)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-disband"));
            return;
        }
        List<String> replaced = plugin.getMessages().get("successfully-disband").stream()
                .map(s -> s.replace("%clan_tag%", tag))
                .collect(Collectors.toList());
        plugin.getDataBase().deleteClan(tag).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        ActionType.dispatchAll(player, replaced)
                )
        );
    }

    @Override
    public @NotNull String getName() {
        return "disband";
    }
}
