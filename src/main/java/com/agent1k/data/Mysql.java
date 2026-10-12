package com.agent1k.data;

import com.agent1k.GlowClans;
import com.agent1k.api.event.ClanCreateEvent;
import com.agent1k.api.event.ClanDisbandEvent;
import com.agent1k.api.event.ClanMemberJoinEvent;
import com.agent1k.api.event.ClanMemberLeaveEvent;
import com.agent1k.config.DbConfig;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Mysql {

    private final GlowClans plugin;
    private final String prefix;
    private Connection connection;

    private final Map<UUID, String> playerClanCache = new HashMap<>();
    private final Map<String, UUID> clanOwnerCache = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>();
    private final Map<String, Inventory> chestCache = new HashMap<>();
    private final Map<Inventory, String> chestToClanCache = new IdentityHashMap<>();
    private final Map<String, ClanHome> homeCache = new HashMap<>();
    private final Map<String, Integer> killsCache = new HashMap<>();
    private final Map<String, Double> balanceCache = new HashMap<>();
    private final Map<String, Integer> unlockedSlotsCache = new HashMap<>();

    public Mysql(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getDbConfig().getTablePrefix();
        connect();
        createTables();
        loadCache();
    }

    private @NotNull String t(@NotNull String name) {
        return prefix + name;
    }

    private void connect() {
        DbConfig cfg = plugin.getDbConfig();
        String url = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=%b&autoReconnect=true&characterEncoding=utf8&useUnicode=true",
                cfg.getMysqlHost(), cfg.getMysqlPort(), cfg.getMysqlDatabase(), cfg.isMysqlUseSsl());
        try {
            connection = DriverManager.getConnection(url, cfg.getMysqlUsername(), cfg.getMysqlPassword());
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка подключения к MySQL: " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("clans") + " (" +
                    "tag VARCHAR(64) PRIMARY KEY," +
                    "owner VARCHAR(36) NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("members") + " (" +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "clan_tag VARCHAR(64) NOT NULL," +
                    "FOREIGN KEY(clan_tag) REFERENCES " + t("clans") + "(tag) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("chest_items") + " (" +
                    "clan_tag VARCHAR(64) PRIMARY KEY," +
                    "items LONGTEXT NOT NULL," +
                    "FOREIGN KEY(clan_tag) REFERENCES " + t("clans") + "(tag) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("clan_homes") + " (" +
                    "clan_tag VARCHAR(64) PRIMARY KEY," +
                    "world VARCHAR(255) NOT NULL," +
                    "x DOUBLE NOT NULL," +
                    "y DOUBLE NOT NULL," +
                    "z DOUBLE NOT NULL," +
                    "yaw DOUBLE NOT NULL," +
                    "pitch DOUBLE NOT NULL," +
                    "FOREIGN KEY(clan_tag) REFERENCES " + t("clans") + "(tag) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("clan_kills") + " (" +
                    "clan_tag VARCHAR(64) PRIMARY KEY," +
                    "kills INT NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(clan_tag) REFERENCES " + t("clans") + "(tag) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("clan_balance") + " (" +
                    "clan_tag VARCHAR(64) PRIMARY KEY," +
                    "balance DOUBLE NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(clan_tag) REFERENCES " + t("clans") + "(tag) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + t("clan_chest_slots") + " (" +
                    "clan_tag VARCHAR(64) PRIMARY KEY," +
                    "unlocked_slots INT NOT NULL," +
                    "FOREIGN KEY(clan_tag) REFERENCES " + t("clans") + "(tag) ON DELETE CASCADE)");
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка создания таблиц: " + e.getMessage());
        }
    }

    private void loadCache() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet clans = stmt.executeQuery("SELECT tag, owner FROM " + t("clans"));
            while (clans.next()) {
                String tag = clans.getString("tag");
                UUID owner = UUID.fromString(clans.getString("owner"));
                clanOwnerCache.put(tag, owner);
            }
            ResultSet members = stmt.executeQuery("SELECT player_uuid, clan_tag FROM " + t("members"));
            while (members.next()) {
                UUID uuid = UUID.fromString(members.getString("player_uuid"));
                String tag = members.getString("clan_tag");
                playerClanCache.put(uuid, tag);
            }
            ResultSet homes = stmt.executeQuery("SELECT clan_tag, world, x, y, z, yaw, pitch FROM " + t("clan_homes"));
            while (homes.next()) {
                String tag = homes.getString("clan_tag");
                ClanHome home = new ClanHome(
                        homes.getString("world"),
                        homes.getDouble("x"),
                        homes.getDouble("y"),
                        homes.getDouble("z"),
                        homes.getFloat("yaw"),
                        homes.getFloat("pitch")
                );
                homeCache.put(tag, home);
            }
            ResultSet kills = stmt.executeQuery("SELECT clan_tag, kills FROM " + t("clan_kills"));
            while (kills.next()) {
                killsCache.put(kills.getString("clan_tag"), kills.getInt("kills"));
            }
            ResultSet balances = stmt.executeQuery("SELECT clan_tag, balance FROM " + t("clan_balance"));
            while (balances.next()) {
                balanceCache.put(balances.getString("clan_tag"), balances.getDouble("balance"));
            }
            ResultSet slots = stmt.executeQuery("SELECT clan_tag, unlocked_slots FROM " + t("clan_chest_slots"));
            while (slots.next()) {
                unlockedSlotsCache.put(slots.getString("clan_tag"), slots.getInt("unlocked_slots"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка загрузки кэша: " + e.getMessage());
        }
    }

    public int getUnlockedSlots(@NotNull String tag) {
        return unlockedSlotsCache.getOrDefault(tag, plugin.getConfigs().getDefaultUnlock());
    }

    public void setUnlockedSlots(@NotNull String tag, int slots) {
        unlockedSlotsCache.put(tag, slots);
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("clan_chest_slots") + " (clan_tag, unlocked_slots) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE unlocked_slots = VALUES(unlocked_slots)")) {
                stmt.setString(1, tag);
                stmt.setInt(2, slots);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка сохранения слотов: " + e.getMessage());
            }
        });
    }

    public @NotNull CompletableFuture<Void> createClan(@NotNull String tag, @NotNull UUID owner) {
        clanOwnerCache.put(tag, owner);
        playerClanCache.put(owner, tag);
        killsCache.put(tag, 0);
        balanceCache.put(tag, 0.0);
        Bukkit.getPluginManager().callEvent(new ClanCreateEvent(tag, owner));
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement clanStmt = connection.prepareStatement(
                    "INSERT INTO " + t("clans") + " (tag, owner) VALUES (?, ?)");
                 PreparedStatement memberStmt = connection.prepareStatement(
                         "INSERT INTO " + t("members") + " (player_uuid, clan_tag) VALUES (?, ?)");
                 PreparedStatement killsStmt = connection.prepareStatement(
                         "INSERT INTO " + t("clan_kills") + " (clan_tag, kills) VALUES (?, 0)");
                 PreparedStatement balanceStmt = connection.prepareStatement(
                         "INSERT INTO " + t("clan_balance") + " (clan_tag, balance) VALUES (?, 0)")) {
                clanStmt.setString(1, tag);
                clanStmt.setString(2, owner.toString());
                clanStmt.execute();
                memberStmt.setString(1, owner.toString());
                memberStmt.setString(2, tag);
                memberStmt.execute();
                killsStmt.setString(1, tag);
                killsStmt.execute();
                balanceStmt.setString(1, tag);
                balanceStmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка создания клана: " + e.getMessage());
            }
        });
    }

    public @NotNull CompletableFuture<Void> deleteClan(@NotNull String tag) {
        List<UUID> members = getMemberUuids(tag);
        UUID owner = clanOwnerCache.remove(tag);
        playerClanCache.values().removeIf(t -> t.equals(tag));
        Inventory removedChest = chestCache.remove(tag);
        if (removedChest != null) chestToClanCache.remove(removedChest);
        homeCache.remove(tag);
        killsCache.remove(tag);
        balanceCache.remove(tag);
        unlockedSlotsCache.remove(tag);
        if (owner != null) {
            Bukkit.getPluginManager().callEvent(new ClanDisbandEvent(tag, owner, members));
        }
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + t("clans") + " WHERE tag = ?")) {
                stmt.setString(1, tag);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка удаления клана: " + e.getMessage());
            }
        });
    }

    public @NotNull CompletableFuture<Void> addMember(@NotNull UUID player, @NotNull String tag) {
        playerClanCache.put(player, tag);
        Bukkit.getPluginManager().callEvent(new ClanMemberJoinEvent(tag, player));
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("members") + " (player_uuid, clan_tag) VALUES (?, ?)")) {
                stmt.setString(1, player.toString());
                stmt.setString(2, tag);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка добавления участника: " + e.getMessage());
            }
        });
    }

    public @NotNull CompletableFuture<Void> removeMember(@NotNull UUID player) {
        String previousTag = playerClanCache.remove(player);
        if (previousTag != null) {
            Bukkit.getPluginManager().callEvent(new ClanMemberLeaveEvent(previousTag, player));
        }
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM " + t("members") + " WHERE player_uuid = ?")) {
                stmt.setString(1, player.toString());
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка удаления участника: " + e.getMessage());
            }
        });
    }

    public void incrementKills(@NotNull String tag) {
        killsCache.merge(tag, 1, Integer::sum);
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("clan_kills") + " (clan_tag, kills) VALUES (?, 1) " +
                            "ON DUPLICATE KEY UPDATE kills = kills + 1")) {
                stmt.setString(1, tag);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка обновления убийств: " + e.getMessage());
            }
        });
    }

    public int getKills(@NotNull String tag) {
        return killsCache.getOrDefault(tag, 0);
    }

    public double getBalance(@NotNull String tag) {
        return balanceCache.getOrDefault(tag, 0.0);
    }

    public @NotNull CompletableFuture<Void> addBalance(@NotNull String tag, double amount) {
        balanceCache.merge(tag, amount, Double::sum);
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("clan_balance") + " (clan_tag, balance) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE balance = balance + ?")) {
                stmt.setString(1, tag);
                stmt.setDouble(2, amount);
                stmt.setDouble(3, amount);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка пополнения баланса: " + e.getMessage());
            }
        });
    }

    public @NotNull CompletableFuture<Void> subtractBalance(@NotNull String tag, double amount) {
        balanceCache.merge(tag, -amount, Double::sum);
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("clan_balance") + " (clan_tag, balance) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE balance = balance - ?")) {
                stmt.setString(1, tag);
                stmt.setDouble(2, 0.0);
                stmt.setDouble(3, amount);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка снятия баланса: " + e.getMessage());
            }
        });
    }

    public @NotNull CompletableFuture<Void> saveHome(@NotNull String tag, @NotNull ClanHome home) {
        homeCache.put(tag, home);
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("clan_homes") + " (clan_tag, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE world=VALUES(world), x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch)")) {
                stmt.setString(1, tag);
                stmt.setString(2, home.toLocation().getWorld().getName());
                stmt.setDouble(3, home.toLocation().getX());
                stmt.setDouble(4, home.toLocation().getY());
                stmt.setDouble(5, home.toLocation().getZ());
                stmt.setFloat(6, home.toLocation().getYaw());
                stmt.setFloat(7, home.toLocation().getPitch());
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка сохранения дома: " + e.getMessage());
            }
        });
    }

    public @NotNull Optional<ClanHome> getHome(@NotNull String tag) {
        return Optional.ofNullable(homeCache.get(tag));
    }

    public @NotNull CompletableFuture<Inventory> loadChest(@NotNull String tag) {
        Inventory cached = chestCache.get(tag);
        if (cached != null) return CompletableFuture.completedFuture(cached);
        Inventory inventory = plugin.getServer().createInventory(
                null,
                plugin.getConfigs().getChestSize(),
                plugin.getConfigs().getChestInventoryName()
        );
        chestCache.put(tag, inventory);
        chestToClanCache.put(inventory, tag);
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT items FROM " + t("chest_items") + " WHERE clan_tag = ?")) {
                stmt.setString(1, tag);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    ItemStack[] contents = deserializeItems(rs.getString("items"));
                    inventory.setContents(contents);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка загрузки сундука: " + e.getMessage());
            }
            return inventory;
        });
    }

    public @NotNull CompletableFuture<Void> saveChest(@NotNull String tag, @NotNull Inventory inventory) {
        Inventory previous = chestCache.put(tag, inventory);
        if (previous != null && previous != inventory) chestToClanCache.remove(previous);
        chestToClanCache.put(inventory, tag);
        return CompletableFuture.runAsync(() -> {
            String serialized = serializeItems(inventory.getContents());
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO " + t("chest_items") + " (clan_tag, items) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE items = VALUES(items)")) {
                stmt.setString(1, tag);
                stmt.setString(2, serialized);
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка сохранения сундука: " + e.getMessage());
            }
        });
    }

    public boolean isChestInventory(@NotNull Inventory inventory) {
        return chestToClanCache.containsKey(inventory);
    }

    public @NotNull Optional<String> getClanByChest(@NotNull Inventory inventory) {
        return Optional.ofNullable(chestToClanCache.get(inventory));
    }

    private @NotNull String serializeItems(@NotNull ItemStack[] items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сериализации предметов: " + e.getMessage());
            return "";
        }
    }

    private @NotNull ItemStack[] deserializeItems(@NotNull String data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().severe("Ошибка десериализации предметов: " + e.getMessage());
            return new ItemStack[plugin.getConfigs().getChestSize()];
        }
    }

    public int getMemberCount(@NotNull String tag) {
        return (int) playerClanCache.values().stream().filter(t -> t.equals(tag)).count();
    }

    public @NotNull List<UUID> getMemberUuids(@NotNull String tag) {
        List<UUID> result = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : playerClanCache.entrySet()) {
            if (entry.getValue().equals(tag)) result.add(entry.getKey());
        }
        return result;
    }

    public @NotNull List<String> getClanTags() {
        return new ArrayList<>(clanOwnerCache.keySet());
    }

    public @NotNull Optional<String> getClanTag(@NotNull UUID player) {
        return Optional.ofNullable(playerClanCache.get(player));
    }

    public @NotNull Optional<UUID> getClanOwner(@NotNull String tag) {
        return Optional.ofNullable(clanOwnerCache.get(tag));
    }

    public boolean isOwner(@NotNull UUID player, @NotNull String tag) {
        return player.equals(clanOwnerCache.get(tag));
    }

    public boolean hasClan(@NotNull UUID player) {
        return playerClanCache.containsKey(player);
    }

    public boolean clanExists(@NotNull String tag) {
        return clanOwnerCache.containsKey(tag);
    }

    public void addInvite(@NotNull UUID inviter, @NotNull String tag) {
        pendingInvites.put(inviter, tag);
    }

    public @NotNull Optional<String> getInvite(@NotNull UUID inviter) {
        return Optional.ofNullable(pendingInvites.get(inviter));
    }

    public void removeInvite(@NotNull UUID inviter) {
        pendingInvites.remove(inviter);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка закрытия соединения: " + e.getMessage());
        }
    }
}
