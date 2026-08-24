package com.agent1k.gui.menu;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItem {

    private final String name;
    private final List<String> lore;
    private final int slot;
    private final Material material;

    public MenuItem(@NotNull String name, @NotNull List<String> lore, int slot, @NotNull Material material) {
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.material = material;
    }

    public @NotNull String getName() { return name; }
    public @NotNull List<String> getLore() { return lore; }
    public int getSlot() { return slot; }
    public @NotNull Material getMaterial() { return material; }
}
