package com.agent1k.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClanCreateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String tag;
    private final UUID owner;

    public ClanCreateEvent(@NotNull String tag, @NotNull UUID owner) {
        this.tag = tag;
        this.owner = owner;
    }

    public @NotNull String getTag() { return tag; }
    public @NotNull UUID getOwner() { return owner; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
