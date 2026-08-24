package com.agent1k.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ActionType {
    MESSAGE("message", new MessageAction()),
    SOUND("sound", new SoundAction()),
    COMMAND("command", new CommandAction()),
    CONSOLE("console", new ConsoleAction());

    private final String key;
    private final Action action;

    ActionType(@NotNull String key, @NotNull Action action) {
        this.key = key;
        this.action = action;
    }

    public @NotNull String getKey() {
        return key;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public static void dispatch(@NotNull Player player, @NotNull String raw) {
        for (ActionType type : values()) {
            String prefix = "[" + type.key + "] ";
            if (raw.startsWith(prefix)) {
                type.action.execute(player, raw.substring(prefix.length()));
                return;
            }
        }
    }

    public static void dispatchAll(@NotNull Player player, @NotNull List<String> lines) {
        for (String line : lines) {
            if (line.isEmpty()) {
                player.sendMessage("");
            } else {
                dispatch(player, line);
            }
        }
    }
}
