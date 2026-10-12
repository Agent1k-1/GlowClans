package com.agent1k.gui.menu.panel.item;

import com.agent1k.click.ClickType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MenuItem {

    private final String name;
    private final List<String> lore;
    private final int slot;
    private final Material material;
    private final Map<ClickType, List<String>> clickActions;

    public MenuItem(@NotNull String name, @NotNull List<String> lore, int slot,
                    @NotNull Material material, @NotNull Map<ClickType, List<String>> clickActions) {
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.material = material;
        this.clickActions = clickActions;
    }

    public @NotNull String getName() { return name; }
    public @NotNull List<String> getLore() { return lore; }
    public int getSlot() { return slot; }
    public @NotNull Material getMaterial() { return material; }

    public @NotNull List<String> getClickActions(@NotNull ClickType type) {
        return clickActions.getOrDefault(type, Collections.emptyList());
    }
}
