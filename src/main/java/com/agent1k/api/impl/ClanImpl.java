package com.agent1k.api.impl;

import com.agent1k.GlowClans;
import com.agent1k.api.clan.Clan;
import com.agent1k.data.ClanHome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ClanImpl implements Clan {

    private final GlowClans plugin;
    private final String tag;

    ClanImpl(@NotNull GlowClans plugin, @NotNull String tag) {
        this.plugin = plugin;
        this.tag = tag;
    }

    static void ensureMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Изменения через GlowClans API нужно выполнять из основного потока сервера");
        }
    }

    private void ensureExists() {
        if (!exists()) {
            throw new IllegalStateException("Клан " + tag + " больше не существует");
        }
    }

    @Override
    public @NotNull String getTag() {
        return tag;
    }

    @Override
    public boolean exists() {
        return plugin.getDataBase().clanExists(tag);
    }

    @Override
    public @NotNull UUID getOwner() {
        return plugin.getDataBase().getClanOwner(tag)
                .orElseThrow(() -> new IllegalStateException("Клан " + tag + " больше не существует"));
    }

    @Override
    public @NotNull List<UUID> getMembers() {
        return plugin.getDataBase().getMemberUuids(tag);
    }

    @Override
    public int getMemberCount() {
        return plugin.getDataBase().getMemberCount(tag);
    }

    @Override
    public boolean isMember(@NotNull UUID player) {
        return plugin.getDataBase().getClanTag(player).filter(tag::equals).isPresent();
    }

    @Override
    public boolean isOwner(@NotNull UUID player) {
        return plugin.getDataBase().isOwner(player, tag);
    }

    @Override
    public int getKills() {
        return plugin.getDataBase().getKills(tag);
    }

    @Override
    public double getBalance() {
        return plugin.getDataBase().getBalance(tag);
    }

    @Override
    public int getUnlockedSlots() {
        return plugin.getDataBase().getUnlockedSlots(tag);
    }

    @Override
    public @NotNull Optional<Location> getHome() {
        return plugin.getDataBase().getHome(tag)
                .map(ClanHome::toLocation)
                .filter(location -> location.getWorld() != null);
    }

    @Override
    public @NotNull CompletableFuture<Void> addMember(@NotNull UUID player) {
        ensureMainThread();
        ensureExists();
        if (plugin.getDataBase().hasClan(player)) {
            throw new IllegalStateException("Игрок " + player + " уже состоит в клане");
        }
        return plugin.getDataBase().addMember(player, tag);
    }

    @Override
    public @NotNull CompletableFuture<Void> removeMember(@NotNull UUID player) {
        ensureMainThread();
        ensureExists();
        if (isOwner(player)) {
            throw new IllegalArgumentException("Владельца нельзя убрать из клана, вместо этого распустите клан");
        }
        if (!isMember(player)) {
            throw new IllegalArgumentException("Игрок " + player + " не состоит в клане " + tag);
        }
        return plugin.getDataBase().removeMember(player);
    }

    @Override
    public @NotNull CompletableFuture<Void> addBalance(double amount) {
        ensureMainThread();
        ensureExists();
        requirePositive(amount);
        return plugin.getDataBase().addBalance(tag, amount);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> subtractBalance(double amount) {
        ensureMainThread();
        ensureExists();
        requirePositive(amount);
        if (getBalance() < amount) {
            return CompletableFuture.completedFuture(false);
        }
        return plugin.getDataBase().subtractBalance(tag, amount).thenApply(ignored -> true);
    }

    @Override
    public void incrementKills() {
        ensureMainThread();
        ensureExists();
        plugin.getDataBase().incrementKills(tag);
    }

    @Override
    public void setUnlockedSlots(int slots) {
        ensureMainThread();
        ensureExists();
        int max = plugin.getConfigs().getChestSize();
        if (slots < 0 || slots > max) {
            throw new IllegalArgumentException("Количество слотов должно быть от 0 до " + max);
        }
        plugin.getDataBase().setUnlockedSlots(tag, slots);
    }

    @Override
    public @NotNull CompletableFuture<Void> setHome(@NotNull Location location) {
        ensureMainThread();
        ensureExists();
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("У локации должен быть указан мир");
        }
        return plugin.getDataBase().saveHome(tag, new ClanHome(location));
    }

    @Override
    public @NotNull CompletableFuture<Void> disband() {
        ensureMainThread();
        ensureExists();
        return plugin.getDataBase().deleteClan(tag);
    }

    private static void requirePositive(double amount) {
        if (!(amount > 0) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Сумма должна быть положительным конечным числом");
        }
    }
}
