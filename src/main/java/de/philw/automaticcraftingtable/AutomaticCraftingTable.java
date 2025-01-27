package de.philw.automaticcraftingtable;

import de.philw.automaticcraftingtable.commands.ACTCommand;
import de.philw.automaticcraftingtable.commands.GetACTCommand;
import de.philw.automaticcraftingtable.holder.CraftingTableLimit;
import de.philw.automaticcraftingtable.holder.RegisteredCraftingTableHolder;
import de.philw.automaticcraftingtable.holder.UsedRecipesHolder;
import de.philw.automaticcraftingtable.listener.BreakBlockListener;
import de.philw.automaticcraftingtable.listener.CraftingTableEditUIListener;
import de.philw.automaticcraftingtable.listener.CraftingTableLeftClickListener;
import de.philw.automaticcraftingtable.listener.PlaceACTListener;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.task.CheckHopperTask;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import de.philw.automaticcraftingtable.util.RecipeUtil;
//import org.bstats.bukkit.Metrics;
import de.philw.automaticcraftingtable.database.SQLiteUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class AutomaticCraftingTable extends JavaPlugin {

    private CraftingTableManager craftingTableManager;
    private RecipeUtil recipeUtil;

    @Override
    public void onEnable() {
    //    new Metrics(this, 19035);

        printBInviteArt();
        // 初始化数据库路径
        SQLiteUtil.initialize(this);

        // 创建数据库表
        SQLiteUtil.createTable();
        getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §aSqlite数据库初始化完毕");

        ConfigManager.setUpConfig(this);
        if (!ConfigManager.getEnabled()) {
            Bukkit.getServer().getLogger().warning(getMessageBeginning() + "Plugin has not been enabled. Change it it config.yml if you want!");
            return;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI")!= null) {
            new RegisteredCraftingTableHolder(this).register();
            new UsedRecipesHolder(this).register();
            new CraftingTableLimit(this).register();
            getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §aPlaceholderAPI占位符注册完毕");
        }
        if (ConfigManager.isSeparateFromOtherCraftingTables()) {
            ACTBlockUTIL.automaticCraftingTable = this;
            Bukkit.getPluginManager().registerEvents(new PlaceACTListener(this), this);
            Objects.requireNonNull(getCommand("getACT")).setExecutor(new GetACTCommand(this));
        }
        this.getCommand("act").setExecutor(new ACTCommand(this));
        this.getCommand("act").setTabCompleter(new ACTCommand(this));
        getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §a命令处理器注册完毕");
        try {
            craftingTableManager = new CraftingTableManager(this);
            getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §a自动合成管理器模块注册完毕");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recipeUtil = new RecipeUtil(this);
        getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §a配方模块注册完毕");
        Bukkit.getPluginManager().registerEvents(new CraftingTableLeftClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableEditUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlockListener(this), this);
        getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §a玩家交互工作台模块注册完毕");
        int timer = ConfigManager.getTimer();

        Bukkit.getScheduler().runTaskTimer(this, new CheckHopperTask(this), timer, timer);

        getServer().getConsoleSender().sendMessage("§8[ §a✔§8 ] §a____ , 启动!");

        if (ConfigManager.isSeparateFromOtherCraftingTables()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                craftingTableManager.registerAutomaticCraftingTables();
            });
        }


    }

    @Override
    public void onDisable() {
        craftingTableManager.unregisterAutomaticCraftingTables();
    }

    public CraftingTableManager getCraftingTableManager() {
        return craftingTableManager;
    }

    public RecipeUtil getRecipeUtil() {
        return recipeUtil;
    }

    public static String getMessageBeginning() {
        return "[AutomaticCraftingTable] ";
    }

    private void printBInviteArt() {
        String art = "\n\n§b"+
"██████╗  █████╗  ██████╗████████╗\n"+
"██╔══██╗██╔══██╗██╔════╝╚══██╔══╝\n"+
"██████╔╝███████║██║        ██║            定制更多插件联系QQ366262436！\n"+
"██╔══██╗██╔══██║██║        ██║\n"+
"██████╔╝██║  ██║╚██████╗   ██║\n"+
"╚═════╝ ╚═╝  ╚═╝ ╚═════╝   ╚═╝\n";

        // 输出字符画
        getServer().getConsoleSender().sendMessage(art);
    }

}