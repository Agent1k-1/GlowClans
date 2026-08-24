package com.agent1k.api;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PointsAPI {

    private PlayerPointsAPI api;
    private boolean enabled = false;

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") instanceof PlayerPoints) {
            PlayerPoints pp = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
            api = pp.getAPI();
            enabled = true;
        }
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int look(@NotNull Player player) {
        if (!enabled) return 0;
        return api.look(player.getUniqueId());
    }

    public boolean take(@NotNull Player player, int amount) {
        if (!enabled) return false;
        return api.take(player.getUniqueId(), amount);
    }

    public boolean give(@NotNull Player player, int amount) {
        if (!enabled) return false;
        return api.give(player.getUniqueId(), amount);
    }
}
