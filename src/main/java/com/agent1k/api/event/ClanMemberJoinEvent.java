package com.agent1k.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClanMemberJoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String tag;
    private final UUID player;

    public ClanMemberJoinEvent(@NotNull String tag, @NotNull UUID player) {
        this.tag = tag;
        this.player = player;
    }

    public @NotNull String getTag() { return tag; }
    public @NotNull UUID getPlayer() { return player; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
