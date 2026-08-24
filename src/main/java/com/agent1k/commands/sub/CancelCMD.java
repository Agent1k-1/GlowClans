package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CancelCMD implements subcommand {

    private final GlowClans plugin;

    public CancelCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("no-clan-help"));
            return;
        }
        Player inviter = Bukkit.getPlayer(args[1]);
        if (inviter == null) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-cancel-clan"));
            return;
        }
        Optional<String> inviteOpt = plugin.getDataBase().getInvite(inviter.getUniqueId());
        if (!inviteOpt.isPresent()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-cancel-clan"));
            return;
        }
        plugin.getDataBase().removeInvite(inviter.getUniqueId());
        ActionType.dispatchAll(player, plugin.getMessages().get("successfully-cancel"));
        ActionType.dispatchAll(inviter, plugin.getMessages().get("owner-clan-cancel"));
    }

    @Override
    public @NotNull String getName() {
        return "cancel";
    }
}
