package com.agent1k.click;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public enum ClickType {
    LEFT_CLICK_COMMANDS("left_click_commands") {
        @Override
        public boolean matches(@NotNull InventoryClickEvent event) {
            return event.isLeftClick();
        }
    },
    RIGHT_CLICK_COMMANDS("right_click_commands") {
        @Override
        public boolean matches(@NotNull InventoryClickEvent event) {
            return event.isRightClick();
        }
    };

    private final String key;

    ClickType(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }

    public abstract boolean matches(@NotNull InventoryClickEvent event);
}
