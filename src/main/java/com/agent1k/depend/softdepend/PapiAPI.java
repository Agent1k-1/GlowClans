package com.agent1k.depend.softdepend;

import com.agent1k.GlowClans;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PapiAPI extends PlaceholderExpansion {

    private static String currentMessage = "";

    private final GlowClans plugin;

    public PapiAPI(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    public static void setCurrentMessage(@NotNull String message) {
        currentMessage = message;
    }

    public static void clearCurrentMessage() {
        currentMessage = "";
    }

    @Override
    public @NotNull String getIdentifier() { return "clan"; }

    @Override
    public @NotNull String getAuthor() { return "agent1k"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        Optional<String> tagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        switch (params) {
            case "tag":
                return tagOpt.orElseGet(() -> plugin.getConfigs().isClanNoTagEnabled()
                        ? plugin.getConfigs().getClanNoTagValue()
                        : "");
            case "owner":
                return tagOpt.flatMap(tag -> plugin.getDataBase().getClanOwner(tag))
                        .map(uuid -> {
                            Player online = Bukkit.getPlayer(uuid);
                            if (online != null) return online.getName();
                            return Bukkit.getOfflinePlayer(uuid).getName();
                        })
                        .orElse("");
            case "kill":
                return tagOpt.map(tag -> String.valueOf(plugin.getDataBase().getKills(tag)))
                        .orElse("0");
            case "member_list":
                return tagOpt.map(tag -> String.valueOf(plugin.getDataBase().getMemberCount(tag)))
                        .orElse("0");
            case "balance":
                return tagOpt.map(tag -> String.valueOf(plugin.getDataBase().getBalance(tag)))
                        .orElse("0");
            case "enderchest_upgrade_amount":
                return String.valueOf((int) plugin.getConfigs().getUpgradeAmount());
            case "message":
                return currentMessage;
            default:
                return null;
        }
    }
}
