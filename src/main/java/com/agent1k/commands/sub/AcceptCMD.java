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

public class AcceptCMD implements subcommand {

    private final GlowClans plugin;

    public AcceptCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        Player inviter = Bukkit.getPlayer(args[1]);
        if (inviter == null) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-accept"));
            return;
        }
        Optional<String> inviteOpt = plugin.getDataBase().getInvite(inviter.getUniqueId());
        if (inviteOpt.isEmpty()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-accept"));
            return;
        }
        String tag = inviteOpt.get();
        plugin.getDataBase().removeInvite(inviter.getUniqueId());
        plugin.getDataBase().addMember(player.getUniqueId(), tag).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ActionType.dispatchAll(player, plugin.getMessages().get("successfully-accept"));
                    ActionType.dispatchAll(inviter, plugin.getMessages().get("player-accept-clan"));
                })
        );
    }

    @Override
    public @NotNull String getName() {
        return "accept";
    }
}
