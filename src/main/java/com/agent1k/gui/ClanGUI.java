package com.agent1k.gui;

import com.agent1k.GlowClans;
import com.agent1k.gui.menu.MenuConfig;
import com.agent1k.gui.menu.MenuHolder;
import com.agent1k.gui.menu.MenuItem;
import com.agent1k.gui.menu.MenuPanel;
import com.agent1k.utils.HexUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClanGUI {

    private final GlowClans plugin;

    public ClanGUI(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player player) {
        MenuConfig cfg = plugin.getMenuConfig();
        MenuHolder holder = new MenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, cfg.getSize(), parse(player, cfg.getMenuName()));
        holder.setInventory(inventory);

        int size = cfg.getSize();

        for (MenuPanel panel : cfg.getPanels()) {
            ItemStack panelItem = buildItem(player, panel.getMaterial(), panel.getName(), panel.getLore());
            for (int slot : panel.getSlots()) {
                if (slot >= 0 && slot < size) {
                    inventory.setItem(slot, panelItem.clone());
                }
            }
        }

        for (MenuItem item : cfg.getItems().values()) {
            int slot = item.getSlot();
            if (slot >= 0 && slot < size) {
                inventory.setItem(slot, buildItem(player, item.getMaterial(), item.getName(), item.getLore()));
                holder.putItem(slot, item);
            }
        }

        player.openInventory(inventory);
    }

    private @NotNull ItemStack buildItem(@NotNull Player player, @NotNull Material material,
                                         @NotNull String name, @NotNull List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(parse(player, name));
            if (!lore.isEmpty()) meta.setLore(parseAll(player, lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    private @NotNull String parse(@NotNull Player player, @NotNull String text) {
        String result = text;
        org.bukkit.plugin.Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            result = PlaceholderAPI.setPlaceholders(player, result);
        }
        return HexUtils.colorize(result);
    }

    private @NotNull List<String> parseAll(@NotNull Player player, @NotNull List<String> lines) {
        if (lines.isEmpty()) return lines;
        List<String> result = new ArrayList<>(lines.size());
        for (String line : lines) result.add(parse(player, line));
        return result;
    }
}
