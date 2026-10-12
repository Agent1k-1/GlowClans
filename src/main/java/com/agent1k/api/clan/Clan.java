package com.agent1k.api.clan;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Clan {

    @NotNull String getTag();

    boolean exists();

    @NotNull UUID getOwner();

    @NotNull List<UUID> getMembers();

    int getMemberCount();

    boolean isMember(@NotNull UUID player);

    boolean isOwner(@NotNull UUID player);

    int getKills();

    double getBalance();

    int getUnlockedSlots();

    @NotNull Optional<Location> getHome();

    @NotNull CompletableFuture<Void> addMember(@NotNull UUID player);

    @NotNull CompletableFuture<Void> removeMember(@NotNull UUID player);

    @NotNull CompletableFuture<Void> addBalance(double amount);

    @NotNull CompletableFuture<Boolean> subtractBalance(double amount);

    void incrementKills();

    void setUnlockedSlots(int slots);

    @NotNull CompletableFuture<Void> setHome(@NotNull Location location);

    @NotNull CompletableFuture<Void> disband();
}
