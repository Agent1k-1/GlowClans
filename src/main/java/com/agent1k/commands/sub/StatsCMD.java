package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatsCMD implements subcommand {

    private final GlowClans plugin;

    public StatsCMD(@NotNull GlowClans plugin) {
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
        String ownerName = plugin.getDataBase().getClanOwner(tag)
                .map(uuid -> {
                    Player online = Bukkit.getPlayer(uuid);
                    if (online != null) {
                        return online.getName();
                    }
                    return Bukkit.getOfflinePlayer(uuid).getName();
                })
                .orElse("вЂ”");
        String kills = String.valueOf(plugin.getDataBase().getKills(tag));
        List<String> replaced = plugin.getMessages().get("clan-stats").stream()
                .map(s -> s.replace("%clan_tag%", tag)
                           .replace("%clan_owner%", ownerName)
                           .replace("%clan_kill%", kills))
                .collect(Collectors.toList());
        ActionType.dispatchAll(player, replaced);
    }

    @Override
    public @NotNull String getName() {
        return "stats";
    }
}
