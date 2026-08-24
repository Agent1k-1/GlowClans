package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WithdrawCMD implements subcommand {

    private final GlowClans plugin;

    public WithdrawCMD(@NotNull GlowClans plugin) {
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
            ActionType.dispatchAll(player, plugin.getMessages().get("error-member-withdraw"));
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
        double maxWithdraw = plugin.getConfigs().getMaxWithdraw();
        if (amount > maxWithdraw) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-limit-withdraw"));
            return;
        }
        double balance = plugin.getDataBase().getBalance(tag);
        if (balance <= 0 || balance < amount) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-withdraw"));
            return;
        }
        plugin.getDataBase().subtractBalance(tag, amount).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getVaultAPI().deposit(player, amount);
                    ActionType.dispatchAll(player, plugin.getMessages().get("successfully-withdraw"));
                })
        );
    }

    @Override
    public @NotNull String getName() {
        return "withdraw";
    }
}
