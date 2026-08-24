package com.agent1k.commands;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface subcommand {

    void execute(@NotNull Player player, @NotNull String[] args);

    @NotNull String getName();
}
