package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InvestCMD implements subcommand {

    private final GlowClans plugin;

    public InvestCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        Optional<String> tagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        if (tagOpt.isEmpty()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-no-clan"));
            return;
        }
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        if (amount <= 0) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        if (!plugin.getVaultAPI().has(player, amount)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-invest"));
            return;
        }
        String tag = tagOpt.get();
        plugin.getVaultAPI().withdraw(player, amount);
        plugin.getDataBase().addBalance(tag, amount).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        ActionType.dispatchAll(player, plugin.getMessages().get("successfully-invest"))
                )
        );
    }

    @Override
    public @NotNull String getName() {
        return "invest";
    }
}