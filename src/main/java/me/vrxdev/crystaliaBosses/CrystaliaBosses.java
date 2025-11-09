package me.vrxdev.crystaliaBosses;

import me.vrxdev.crystaliaBosses.manager.BossManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.sculkdev.sculklibs.util.MessageUtils;

import static org.sculkdev.sculklibs.util.MessageUtils.fixColor;

public final class CrystaliaBosses extends JavaPlugin {

    public static MessageUtils messageUtils;
    public BossManager bossManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        logMessage("&2Enabling plugin...");

        // add missing config options
        getConfig().options().copyDefaults(true);
        saveConfig();

        messageUtils = new MessageUtils(this);

        bossManager = new BossManager(this);

        Bukkit.getPluginManager().registerEvents(new BossListener(this), this);

        logMessage("&2Loaded plugin in &7" + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        logMessage("&2Disabled plugin");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (messageUtils != null) messageUtils.reload();

        if (bossManager != null) bossManager.loadAll();
    }

    public static void logMessage(String message) {
        Bukkit.getConsoleSender().sendMessage("[CrystaliaBosses] " + fixColor(message));
    }
}
