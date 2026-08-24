package com.agent1k.gui.menu;

import com.agent1k.GlowClans;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MenuDirectory {

    private final GlowClans plugin;
    private final File menuFolder;

    public MenuDirectory(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        this.menuFolder = new File(plugin.getDataFolder(), "menu");
        setup();
    }

    private void setup() {
        if (!menuFolder.exists() && !menuFolder.mkdirs()) {
            plugin.getLogger().severe("Не удалось создать папку menu");
            return;
        }
        File menuFile = new File(menuFolder, "menu.yml");
        if (!menuFile.exists()) {
            plugin.saveResource("menu/menu.yml", false);
        }
    }

    public @NotNull File getMenuFolder() {
        return menuFolder;
    }
}
