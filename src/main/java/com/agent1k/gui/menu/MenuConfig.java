package com.agent1k.gui.menu;

import com.agent1k.GlowClans;
import com.agent1k.utils.HexUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuConfig {

    private final GlowClans plugin;
    private String menuName;
    private int size;
    private String panelName;
    private List<String> panelLore;
    private List<Integer> panelSlots;
    private Material panelMaterial;
    private MenuItem infoItem;

    public MenuConfig(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "menu/menu.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        menuName = HexUtils.colorize(config.getString("name"));
        size = config.getInt("size");
        panelName = HexUtils.colorize(config.getString("panel.name"));
        panelLore = colorizeAll(config.getStringList("panel.lore"));
        panelSlots = new ArrayList<>(config.getIntegerList("panel.slot"));
        panelMaterial = Material.valueOf(config.getString("panel.material"));
        infoItem = loadItem(config, "item-settings.info");
    }

    private @NotNull MenuItem loadItem(@NotNull FileConfiguration config, @NotNull String path) {
        String name = HexUtils.colorize(config.getString(path + ".name"));
        List<String> lore = colorizeAll(config.getStringList(path + ".lore"));
        int slot = config.getInt(path + ".slot");
        Material material = Material.valueOf(config.getString(path + ".material"));
        return new MenuItem(name, lore, slot, material);
    }

    private @NotNull List<String> colorizeAll(@NotNull List<String> lines) {
        if (lines.isEmpty()) return Collections.emptyList();
        List<String> result = new ArrayList<>(lines.size());
        for (String line : lines) result.add(HexUtils.colorize(line));
        return result;
    }

    public @NotNull String getMenuName() { return menuName; }
    public int getSize() { return size; }
    public @NotNull String getPanelName() { return panelName; }
    public @NotNull List<String> getPanelLore() { return panelLore; }
    public @NotNull List<Integer> getPanelSlots() { return panelSlots; }
    public @NotNull Material getPanelMaterial() { return panelMaterial; }
    public @NotNull MenuItem getInfoItem() { return infoItem; }
}