package com.agent1k.commands.tab;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClanCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "create", "invite", "accept", "disband", "leave", "help", "storage", "chat",
            "sethome", "home", "stats", "kick", "invest", "withdraw", "reload", "menu"
    );

    private static final List<String> PLAYER_ARG_SUBCOMMANDS = Arrays.asList("invite", "accept", "kick");

    private static final List<String> AMOUNT_SUGGESTIONS = Arrays.asList(
            "15", "100", "500", "1000", "5000", "10000", "50000", "100000", "500000", "900000"
    );

    private static final String CLAN_NAME_PLACEHOLDER = "<название клана>";
    private static final String AMOUNT_PLACEHOLDER = "<введите сумму>";
    private static final String MESSAGE_PLACEHOLDER = "<сообщение>";

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("create")) {
                return Collections.singletonList(CLAN_NAME_PLACEHOLDER);
            }
            if (PLAYER_ARG_SUBCOMMANDS.contains(sub)) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (sub.equals("invest")) {
                return AMOUNT_SUGGESTIONS.stream()
                        .filter(amount -> amount.startsWith(args[1]))
                        .collect(Collectors.toList());
            }
            if (sub.equals("withdraw")) {
                return Collections.singletonList(AMOUNT_PLACEHOLDER);
            }
            if (sub.equals("chat")) {
                return Collections.singletonList(MESSAGE_PLACEHOLDER);
            }
        }
        return Collections.emptyList();
    }
}