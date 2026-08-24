package com.agent1k.config;

import com.agent1k.GlowClans;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class BlockNameConfig {

    private final GlowClans plugin;
    private final Set<String> blockedNames = new HashSet<>();

    public BlockNameConfig(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "block-name.yml");
        if (!file.exists()) {
            plugin.saveResource("block-name.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        blockedNames.clear();
        for (String name : config.getStringList("block-name-setting.name")) {
            blockedNames.add(name.toLowerCase());
        }
    }

    public boolean isBlocked(@NotNull String name) {
        return blockedNames.contains(name.toLowerCase());
    }
}
