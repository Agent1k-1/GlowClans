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

public class InviteCMD implements subcommand {

    private final GlowClans plugin;

    public InviteCMD(@NotNull GlowClans plugin) {
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
            ActionType.dispatchAll(player, plugin.getMessages().get("error-invite"));
            return;
        }
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-invite"));
            return;
        }
        plugin.getDataBase().addInvite(player.getUniqueId(), tag);
        List<String> replaced = plugin.getMessages().get("new-invite").stream()
                .map(s -> s.replace("%clan_tag%", tag))
                .collect(Collectors.toList());
        ActionType.dispatchAll(target, replaced);
        ActionType.dispatchAll(player, plugin.getMessages().get("owner-clan-invite"));
    }

    @Override
    public @NotNull String getName() {
        return "invite";
    }
}
