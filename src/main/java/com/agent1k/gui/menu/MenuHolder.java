package com.agent1k.gui.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MenuHolder implements InventoryHolder {

    private Inventory inventory;
    private final Map<Integer, MenuItem> slotItems = new HashMap<>();

    public void setInventory(@NotNull Inventory inventory) {
        this.inventory = inventory;
    }

    public void putItem(int slot, @NotNull MenuItem item) {
        slotItems.put(slot, item);
    }

    public @Nullable MenuItem getItem(int slot) {
        return slotItems.get(slot);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
