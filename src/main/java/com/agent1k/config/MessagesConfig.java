package com.agent1k.config;

import com.agent1k.GlowClans;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesConfig {

    private final GlowClans plugin;
    private FileConfiguration config;
    private final Map<String, List<String>> cache = new HashMap<>();

    public MessagesConfig(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        cache.clear();
    }

    public @NotNull List<String> get(@NotNull String key) {
        List<String> cached = cache.get(key);
        if (cached != null) return cached;
        List<String> loaded = Collections.unmodifiableList(config.getStringList(key));
        cache.put(key, loaded);
        return loaded;
    }
}
