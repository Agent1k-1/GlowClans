package com.agent1k.commands.sub;

import com.agent1k.action.ActionType;
import com.agent1k.GlowClans;
import com.agent1k.api.PapiAPI;
import com.agent1k.commands.subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChatCMD implements subcommand {

    private final GlowClans plugin;

    public ChatCMD(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        Optional<String> tagOpt = plugin.getDataBase().getClanTag(player.getUniqueId());
        if (!tagOpt.isPresent()) {
            ActionType.dispatchAll(player, plugin.getMessages().get("error-no-clan"));
            return;
        }
        if (args.length < 2) {
            ActionType.dispatchAll(player, plugin.getMessages().get("clan-help"));
            return;
        }
        String tag = tagOpt.get();
        StringBuilder message = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            message.append(' ').append(args[i]);
        }
        String rawMessage = message.toString();
        List<String> template = plugin.getMessages().get("clan-chat");
        List<String> rendered = new ArrayList<>(template.size());
        for (String line : template) {
            rendered.add(line
                    .replace("%clan_tag%", tag)
                    .replace("%player_name%", player.getName()));
        }
        PapiAPI.setCurrentMessage(rawMessage);
        try {
            for (UUID memberId : plugin.getDataBase().getMemberUuids(tag)) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    ActionType.dispatchAll(member, rendered);
                }
            }
        } finally {
            PapiAPI.clearCurrentMessage();
        }
        ActionType.dispatchAll(player, plugin.getMessages().get("chat-successfully"));
    }

    @Override
    public @NotNull String getName() {
        return "chat";
    }
}
