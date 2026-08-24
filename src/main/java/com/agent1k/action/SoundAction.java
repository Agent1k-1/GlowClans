package com.agent1k.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundAction implements Action {

    @Override
    public void execute(@NotNull Player player, @NotNull String text) {
        String[] parts = text.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return;
        String soundName = parts[0];
        float volume = parts.length > 1 ? parseFloat(parts[1], 1.0f) : 1.0f;
        float pitch = parts.length > 2 ? parseFloat(parts[2], 1.0f) : 1.0f;
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            player.playSound(player.getLocation(), soundName, volume, pitch);
        }
    }

    @Override
    public @NotNull ActionType getType() {
        return ActionType.SOUND;
    }

    private float parseFloat(@NotNull String value, float fallback) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
