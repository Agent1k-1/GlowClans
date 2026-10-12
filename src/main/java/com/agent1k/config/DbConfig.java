package com.agent1k.config;

import com.agent1k.GlowClans;
import com.agent1k.data.DbType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DbConfig {

    private final GlowClans plugin;

    private DbType type;
    private String sqliteFile;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private boolean mysqlUseSsl;
    private String tablePrefix;

    public DbConfig(@NotNull GlowClans plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "db.yml");
        if (!file.exists()) {
            plugin.saveResource("db.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        type = DbType.fromString(config.getString("database-type.type"));
        sqliteFile = config.getString("sqlite.file", "clan.db");
        mysqlHost = config.getString("mysql.host", "localhost");
        mysqlPort = config.getInt("mysql.port", 3306);
        mysqlDatabase = config.getString("mysql.database", "glowclans");
        mysqlUsername = config.getString("mysql.username", "root");
        mysqlPassword = config.getString("mysql.password", "");
        mysqlUseSsl = config.getBoolean("mysql.use-ssl", false);
        tablePrefix = config.getString("mysql.table-prefix", "");
    }

    public @NotNull DbType getType() { return type; }
    public @NotNull String getSqliteFile() { return sqliteFile; }
    public @NotNull String getMysqlHost() { return mysqlHost; }
    public int getMysqlPort() { return mysqlPort; }
    public @NotNull String getMysqlDatabase() { return mysqlDatabase; }
    public @NotNull String getMysqlUsername() { return mysqlUsername; }
    public @NotNull String getMysqlPassword() { return mysqlPassword; }
    public boolean isMysqlUseSsl() { return mysqlUseSsl; }
    public @NotNull String getTablePrefix() { return tablePrefix; }
}
