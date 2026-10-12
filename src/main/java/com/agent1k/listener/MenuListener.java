package com.agent1k.listener;

import com.agent1k.action.ActionType;
import com.agent1k.click.ClickType;
import com.agent1k.gui.menu.holder.MenuHolder;
import com.agent1k.gui.menu.panel.item.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof MenuHolder)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() != event.getInventory()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        MenuItem item = ((MenuHolder) holder).getItem(event.getSlot());
        if (item == null) return;
        for (ClickType type : ClickType.values()) {
            if (type.matches(event)) {
                List<String> actions = item.getClickActions(type);
                if (!actions.isEmpty()) ActionType.dispatchAll(player, actions);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof MenuHolder) {
            event.setCancelled(true);
        }
    }
}