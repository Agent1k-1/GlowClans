package com.agent1k.commands;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClanCommand implements CommandExecutor {

    private final GlowClans plugin;
    private final Map<String, subcommand> subcommands = new HashMap<>();

    public ClanCommand(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull subcommand sub) {
        subcommands.put(sub.getName(), sub);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            ActionType.dispatchAll(player, plugin.getMessages().get(
                    plugin.getDataBase().hasClan(player.getUniqueId()) ? "clan-help" : "no-clan-help"
            ));
            return true;
        }
        subcommand sub = subcommands.get(args[0].toLowerCase());
        if (sub == null) {
            ActionType.dispatchAll(player, plugin.getMessages().get("no-clan-help"));
            return true;
        }
        sub.execute(player, args);
        return true;
    }
}
