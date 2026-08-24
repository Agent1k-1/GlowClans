package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KickCMD implements subcommand {

    private final GlowClans plugin;

    public KickCMD(@NotNull GlowClans plugin) {
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
            ActionType.dispatchAll(player, plugin.getMessages().get("error-kick-member"));
            return;
        }
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-kick-member"));
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-kick-owner"));
            return;
        }
        Optional<String> targetTag = plugin.getDataBase().getClanTag(target.getUniqueId());
        if (targetTag.isEmpty() || !targetTag.get().equals(tag)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-kick-member"));
            return;
        }
        List<String> kickMsg = plugin.getMessages().get("clan-kick-player").stream()
                .map(s -> s.replace("%clan_tag%", tag))
                .collect(Collectors.toList());
        plugin.getDataBase().removeMember(target.getUniqueId()).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ActionType.dispatchAll(target, kickMsg);
                    ActionType.dispatchAll(player, plugin.getMessages().get("successfully-member-kick"));
                })
        );
    }

    @Override
    public @NotNull String getName() {
        return "kick";
    }
}
