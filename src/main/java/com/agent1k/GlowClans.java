package com.agent1k;

import com.agent1k.api.GlowClansAPI;
import com.agent1k.api.impl.GlowClansAPIImpl;
import com.agent1k.depend.softdepend.PapiAPI;
import com.agent1k.depend.softdepend.PointsAPI;
import com.agent1k.depend.VaultAPI;
import com.agent1k.commands.ClanCommand;
import com.agent1k.commands.sub.*;
import com.agent1k.commands.tab.ClanCompleter;
import com.agent1k.config.Configs;
import com.agent1k.config.BlockNameConfig;
import com.agent1k.config.DbConfig;
import com.agent1k.config.MessagesConfig;
import com.agent1k.data.SQLite;
import com.agent1k.gui.ClanGUI;
import com.agent1k.gui.menu.config.MenuConfig;
import com.agent1k.gui.menu.MenuDirectory;
import com.agent1k.listener.ChestListener;
import com.agent1k.listener.KillListener;
import com.agent1k.listener.MenuListener;
import com.agent1k.upgrade.ChestUpgrade;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GlowClans extends JavaPlugin {

    private Configs configs;
    private MessagesConfig messagesConfig;
    private BlockNameConfig blockNameConfig;
    private DbConfig dbConfig;
    private SQLite sqLite;
    private VaultAPI vaultAPI;
    private PointsAPI pointsAPI;
    private ChestUpgrade chestUpgrade;
    private MenuDirectory menuDirectory;
    private MenuConfig menuConfig;
    private ClanGUI clanGUI;

    private static final String ASCII_LOGO = "\n" +
            "░██████╗░██╗░░░░░░█████╗░░██╗░░░░░░░██╗░█████╗░██╗░░░░░░█████╗░███╗░░██╗░██████╗\n" +
            "██╔════╝░██║░░░░░██╔══██╗░██║░░██╗░░██║██╔══██╗██║░░░░░██╔══██╗████╗░██║██╔════╝\n" +
            "██║░░██╗░██║░░░░░██║░░██║░╚██╗████╗██╔╝██║░░╚═╝██║░░░░░███████║██╔██╗██║╚█████╗░\n" +
            "██║░░╚██╗██║░░░░░██║░░██║░░████╔═████║░██║░░██╗██║░░░░░██╔══██║██║╚████║░╚═══██╗\n" +
            "╚██████╔╝███████╗╚█████╔╝░░╚██╔╝░╚██╔╝░╚█████╔╝███████╗██║░░██║██║░╚███║██████╔╝\n" +
            "░╚═════╝░╚══════╝░╚════╝░░░░╚═╝░░░╚═╝░░░╚════╝░╚══════╝╚═╝░░╚═╝╚═╝░░╚══╝╚═════╝░";

    @Override
    public void onEnable() {
        configs = new Configs(this);
        messagesConfig = new MessagesConfig(this);
        blockNameConfig = new BlockNameConfig(this);
        dbConfig = new DbConfig(this);
        sqLite = new SQLite(this);
        vaultAPI = new VaultAPI();
        chestUpgrade = new ChestUpgrade(this);
        menuDirectory = new MenuDirectory(this);
        menuConfig = new MenuConfig(this);
        clanGUI = new ClanGUI(this);

        if (!vaultAPI.setup()) {
            getLogger().severe("Экономика не найдена отключаю плагин");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        pointsAPI = new PointsAPI();
        if (pointsAPI.setup()) {
            getLogger().info("PlayerPoints подключён");
        } else {
            getLogger().warning("PlayerPoints не найден");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiAPI(this).register();
        }

        getServer().getServicesManager().register(GlowClansAPI.class, new GlowClansAPIImpl(this), this, ServicePriority.Normal);

        ClanCommand clanCommand = new ClanCommand(this);
        clanCommand.register(new CreateCMD(this));
        clanCommand.register(new InviteCMD(this));
        clanCommand.register(new AcceptCMD(this));
        clanCommand.register(new CancelCMD(this));
        clanCommand.register(new DisbandCMD(this));
        clanCommand.register(new LeaveCMD(this));
        clanCommand.register(new HelpCMD(this));
        clanCommand.register(new StorageCMD(this));
        clanCommand.register(new ChatCMD(this));
        clanCommand.register(new SethomeCMD(this));
        clanCommand.register(new HomeCMD(this));
        clanCommand.register(new StatsCMD(this));
        clanCommand.register(new KickCMD(this));
        clanCommand.register(new InvestCMD(this));
        clanCommand.register(new WithdrawCMD(this));
        clanCommand.register(new ReloadCMD(this));
        clanCommand.register(new MenuCMD(this));

        getCommand("clan").setExecutor(clanCommand);
        getCommand("clan").setTabCompleter(new ClanCompleter(this));

        getServer().getPluginManager().registerEvents(new ChestListener(this), this);
        getServer().getPluginManager().registerEvents(new KillListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        Bukkit.getConsoleSender().sendMessage(ASCII_LOGO);
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Плагин: " + ChatColor.GREEN + "включён");
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Версия: " + ChatColor.YELLOW + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Автор: " + ChatColor.AQUA + "Agent1k");
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Telegram: " + ChatColor.LIGHT_PURPLE + "t.me/monstexd");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ASCII_LOGO);
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Плагин: " + ChatColor.RED + "выключен");
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Версия: " + ChatColor.YELLOW + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Автор: " + ChatColor.AQUA + "Agent1k");
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "Telegram: " + ChatColor.LIGHT_PURPLE + "t.me/monstexd");
        Bukkit.getConsoleSender().sendMessage("");
        getServer().getServicesManager().unregisterAll(this);
        if (sqLite != null) sqLite.close();
    }

    public @NotNull Configs getConfigs() { return configs; }
    public @NotNull MessagesConfig getMessages() { return messagesConfig; }
    public @NotNull BlockNameConfig getBlockNameConfig() { return blockNameConfig; }
    public @NotNull DbConfig getDbConfig() { return dbConfig; }
    public @NotNull SQLite getDataBase() { return sqLite; }
    public @NotNull VaultAPI getVaultAPI() { return vaultAPI; }
    public @Nullable PointsAPI getPointsAPI() { return pointsAPI; }
    public @NotNull ChestUpgrade getChestUpgrade() { return chestUpgrade; }
    public @NotNull MenuDirectory getMenuDirectory() { return menuDirectory; }
    public @NotNull MenuConfig getMenuConfig() { return menuConfig; }
    public @NotNull ClanGUI getClanGUI() { return clanGUI; }

    public void reload() {
        configs.load();
        messagesConfig.load();
        blockNameConfig.load();
        dbConfig.load();
        chestUpgrade = new ChestUpgrade(this);
        menuConfig.load();
    }
}