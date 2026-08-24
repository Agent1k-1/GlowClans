package com.agent1k.gui;

import com.agent1k.GlowClans;
import com.agent1k.gui.menu.MenuConfig;
import com.agent1k.gui.menu.MenuItem;
import com.agent1k.utils.HexUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClanGUI {

    public static class Holder implements InventoryHolder {
        private Inventory inventory;

        public void setInventory(@NotNull Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    private final GlowClans plugin;

    public ClanGUI(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player player) {
        MenuConfig cfg = plugin.getMenuConfig();
        Holder holder = new Holder();
        Inventory inventory = Bukkit.createInventory(holder, cfg.getSize(), parse(player, cfg.getMenuName()));
        holder.setInventory(inventory);

        ItemStack panel = buildItem(player, cfg.getPanelMaterial(), cfg.getPanelName(), cfg.getPanelLore());
        int size = cfg.getSize();
        for (int slot : cfg.getPanelSlots()) {
            if (slot >= 0 && slot < size) {
                inventory.setItem(slot, panel.clone());
            }
        }

        MenuItem info = cfg.getInfoItem();
        if (info.getSlot() >= 0 && info.getSlot() < size) {
            inventory.setItem(info.getSlot(), buildItem(player, info.getMaterial(), info.getName(), info.getLore()));
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
