package com.agent1k.api.impl;

import com.agent1k.GlowClans;
import com.agent1k.api.GlowClansAPI;
import com.agent1k.api.clan.Clan;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class GlowClansAPIImpl implements GlowClansAPI {

    private static final int MAX_TAG_LENGTH = 64;

    private final GlowClans plugin;

    public GlowClansAPIImpl(@NotNull GlowClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasClan(@NotNull UUID player) {
        return plugin.getDataBase().hasClan(player);
    }

    @Override
    public boolean clanExists(@NotNull String tag) {
        return plugin.getDataBase().clanExists(tag);
    }

    @Override
    public @NotNull Optional<Clan> getClanByPlayer(@NotNull UUID player) {
        return plugin.getDataBase().getClanTag(player).map(tag -> new ClanImpl(plugin, tag));
    }

    @Override
    public @NotNull Optional<Clan> getClanByTag(@NotNull String tag) {
        if (!plugin.getDataBase().clanExists(tag)) return Optional.empty();
        return Optional.of(new ClanImpl(plugin, tag));
    }

    @Override
    public @NotNull List<Clan> getClans() {
        List<String> tags = plugin.getDataBase().getClanTags();
        List<Clan> clans = new ArrayList<>(tags.size());
        for (String tag : tags) {
            clans.add(new ClanImpl(plugin, tag));
        }
        return clans;
    }

    @Override
    public @NotNull CompletableFuture<Clan> createClan(@NotNull String tag, @NotNull UUID owner) {
        ClanImpl.ensureMainThread();
        if (tag.isEmpty() || tag.length() > MAX_TAG_LENGTH || tag.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Тег должен содержать от 1 до " + MAX_TAG_LENGTH + " символов без пробелов");
        }
        if (plugin.getDataBase().clanExists(tag)) {
            throw new IllegalStateException("Клан " + tag + " уже существует");
        }
        if (plugin.getDataBase().hasClan(owner)) {
            throw new IllegalStateException("Игрок " + owner + " уже состоит в клане");
        }
        return plugin.getDataBase().createClan(tag, owner).thenApply(ignored -> new ClanImpl(plugin, tag));
    }
}
