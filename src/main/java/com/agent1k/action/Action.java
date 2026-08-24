package com.agent1k.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Action {

    void execute(@NotNull Player player, @NotNull String text);

    @NotNull ActionType getType();
}
