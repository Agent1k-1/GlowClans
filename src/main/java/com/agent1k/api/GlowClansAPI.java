package com.agent1k.api;

import com.agent1k.api.clan.Clan;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GlowClansAPI {

    static @NotNull GlowClansAPI getInstance() {
        RegisteredServiceProvider<GlowClansAPI> registration =
                Bukkit.getServicesManager().getRegistration(GlowClansAPI.class);
        if (registration == null) {
            throw new IllegalStateException("GlowClans не включён");
        }
        return registration.getProvider();
    }

    boolean hasClan(@NotNull UUID player);

    boolean clanExists(@NotNull String tag);

    @NotNull Optional<Clan> getClanByPlayer(@NotNull UUID player);

    @NotNull Optional<Clan> getClanByTag(@NotNull String tag);

    @NotNull List<Clan> getClans();

    @NotNull CompletableFuture<Clan> createClan(@NotNull String tag, @NotNull UUID owner);
}
