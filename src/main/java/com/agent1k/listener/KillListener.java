package com.agent1k.listener;

import com.agent1k.GlowClans;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class KillListener implements Listener {

    private final GlowClans plugin;

    public KillListener(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        plugin.getDataBase().getClanTag(killer.getUniqueId())
                .ifPresent(tag -> plugin.getDataBase().incrementKills(tag));
    }
}
