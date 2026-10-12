package com.agent1k.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ClanDisbandEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String tag;
    private final UUID owner;
    private final List<UUID> members;

    public ClanDisbandEvent(@NotNull String tag, @NotNull UUID owner, @NotNull List<UUID> members) {
        this.tag = tag;
        this.owner = owner;
        this.members = Collections.unmodifiableList(members);
    }

    public @NotNull String getTag() { return tag; }
    public @NotNull UUID getOwner() { return owner; }
    public @NotNull List<UUID> getMembers() { return members; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
