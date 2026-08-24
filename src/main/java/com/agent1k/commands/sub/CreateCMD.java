package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.commands.subcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CreateCMD implements subcommand {

    private final GlowClans plugin;

    public CreateCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("no-clan-help"));
            return;
        }
        if (plugin.getDataBase().hasClan(player.getUniqueId())) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-create"));
            return;
        }
        double cost = plugin.getConfigs().getCreateCost();
        if (!plugin.getVaultAPI().has(player, cost)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-create"));
            return;
        }
        String tag = args[1];
        if (plugin.getConfigs().isClanTagLimitEnabled()
                && tag.length() > plugin.getConfigs().getClanTagMaxLength()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-tag"));
            return;
        }
        if (plugin.getBlockNameConfig().isBlocked(tag)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-name-block"));
            return;
        }
        if (plugin.getDataBase().clanExists(tag)) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-clan-name"));
            return;
        }
        plugin.getVaultAPI().withdraw(player, cost);
        plugin.getDataBase().createClan(tag, player.getUniqueId()).thenRun(() ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    List<String> replaced = plugin.getMessages().get("successfully-create").stream()
                            .map(s -> s.replace("%clan_tag%", tag))
                            .collect(Collectors.toList());
                    ActionType.dispatchAll(player, replaced);
                })
        );
    }

    @Override
    public @NotNull String getName() {
        return "create";
    }
}
