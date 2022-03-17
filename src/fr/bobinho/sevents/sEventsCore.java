package fr.bobinho.sevents;

import co.aikar.commands.PaperCommandManager;
import fr.bobinho.sevents.commands.event.koth.KOTHCommand;
import fr.bobinho.sevents.listeners.KOTHListener;
import fr.bobinho.sevents.utils.event.EventManager;
import fr.bobinho.sevents.utils.settings.BSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public class sEventsCore extends JavaPlugin {

    /**
     * Fields
     */
    private static sEventsCore instance;
    private static BSettings eventsSetting;

    /**
     * Gets the sevents core instance
     *
     * @return the sevents core instance
     */
    @Nonnull
    public static sEventsCore getInstance() {
        return instance;
    }

    /**
     * Gets the events settings
     *
     * @return the events settings
     */
    @Nonnull
    public static BSettings getEventsSetting() {
        return eventsSetting;
    }

    /**
     * Enable and initialize the plugin
     */
    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[sTeams] Loading the plugin...");

        //Registers commands and listeners
        registerCommands();
        registerListeners();

        eventsSetting = new BSettings("events");

        EventManager.loadEvents();
    }

    /**
     * Disable the plugin and save data
     */
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[sTeams] Unloading the plugin...");

        EventManager.saveEvents();
    }

    /**
     * Register listeners
     */
    private void registerListeners() {

        //Registers test listener
        Bukkit.getServer().getPluginManager().registerEvents(new KOTHListener(), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully loaded listeners");

        EventManager.saveEvents();
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        final PaperCommandManager commandManager = new PaperCommandManager(this);

        //Registers KOTH command
        commandManager.registerCommand(new KOTHCommand());

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully loaded commands");
    }

}