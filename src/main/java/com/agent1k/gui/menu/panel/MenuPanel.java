package com.agent1k.gui.menu.panel;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuPanel {

    private final String name;
    private final List<String> lore;
    private final List<Integer> slots;
    private final Material material;

    public MenuPanel(@NotNull String name, @NotNull List<String> lore,
                     @NotNull List<Integer> slots, @NotNull Material material) {
        this.name = name;
        this.lore = lore;
        this.slots = slots;
        this.material = material;
    }

    public @NotNull String getName() { return name; }
    public @NotNull List<String> getLore() { return lore; }
    public @NotNull List<Integer> getSlots() { return slots; }
    public @NotNull Material getMaterial() { return material; }
}
