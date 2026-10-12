package com.agent1k.gui.menu.config;

import com.agent1k.GlowClans;
import com.agent1k.click.ClickType;
import com.agent1k.gui.menu.panel.MenuPanel;
import com.agent1k.gui.menu.panel.item.MenuItem;
import com.agent1k.utils.HexUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuConfig {

    private final GlowClans plugin;
    private String menuName;
    private int size;
    private List<MenuPanel> panels;
    private Map<String, MenuItem> items;

    public MenuConfig(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "menu/menu.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        menuName = HexUtils.colorize(config.getString("name"));
        size = config.getInt("size");

        panels = new ArrayList<>();
        for (Map<?, ?> panelMap : config.getMapList("panel")) {
            panels.add(loadPanel(panelMap));
        }

        items = new LinkedHashMap<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("item-settings");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                items.put(key, loadItem(config, "item-settings." + key));
            }
        }
    }

    private @NotNull MenuPanel loadPanel(@NotNull Map<?, ?> map) {
        Object nameObj = map.get("name");
        String name = HexUtils.colorize(nameObj != null ? nameObj.toString() : "");
        List<String> lore = colorizeAll(toStringList(map.get("lore")));
        List<Integer> slots = toIntegerList(map.get("slot"));
        Material material = Material.valueOf(String.valueOf(map.get("material")));
        return new MenuPanel(name, lore, slots, material);
    }

    private @NotNull MenuItem loadItem(@NotNull FileConfiguration config, @NotNull String path) {
        String name = HexUtils.colorize(config.getString(path + ".name"));
        List<String> lore = colorizeAll(config.getStringList(path + ".lore"));
        int slot = config.getInt(path + ".slot");
        Material material = Material.valueOf(config.getString(path + ".material"));
        Map<ClickType, List<String>> clickActions = new EnumMap<>(ClickType.class);
        for (ClickType type : ClickType.values()) {
            List<String> commands = config.getStringList(path + "." + type.getKey());
            if (!commands.isEmpty()) clickActions.put(type, commands);
        }
        return new MenuItem(name, lore, slot, material, clickActions);
    }

    private @NotNull List<String> colorizeAll(@NotNull List<String> lines) {
        if (lines.isEmpty()) return Collections.emptyList();
        List<String> result = new ArrayList<>(lines.size());
        for (String line : lines) result.add(HexUtils.colorize(line));
        return result;
    }

    private @NotNull List<String> toStringList(Object obj) {
        if (!(obj instanceof List)) return Collections.emptyList();
        List<?> raw = (List<?>) obj;
        List<String> result = new ArrayList<>(raw.size());
        for (Object item : raw) result.add(String.valueOf(item));
        return result;
    }

    private @NotNull List<Integer> toIntegerList(Object obj) {
        if (!(obj instanceof List)) return Collections.emptyList();
        List<?> raw = (List<?>) obj;
        List<Integer> result = new ArrayList<>(raw.size());
        for (Object item : raw) {
            if (item instanceof Number) result.add(((Number) item).intValue());
        }
        return result;
    }

    public @NotNull String getMenuName() { return menuName; }
    public int getSize() { return size; }
    public @NotNull List<MenuPanel> getPanels() { return panels; }
    public @NotNull Map<String, MenuItem> getItems() { return items; }
}
