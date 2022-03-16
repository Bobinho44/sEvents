package fr.bobinho.sevents;

import co.aikar.commands.PaperCommandManager;
import fr.bobinho.sevents.utils.settings.BSettings;
import fr.bobinho.steams.commands.team.TeamCommand;
import fr.bobinho.steams.commands.team.TeamsCommand;
import fr.bobinho.steams.listeners.ChatListener;
import fr.bobinho.steams.listeners.TeamListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public class sEventsCore extends JavaPlugin {

    /**
     * Fields
     */
    private static sEventsCore instance;
    private static BSettings teamsSettings;

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
     * Gets the teams settings
     *
     * @return the teams settings
     */
    @Nonnull
    public static BSettings getTeamsSettings() {
        return teamsSettings;
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
    }

    /**
     * Disable the plugin and save data
     */
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[sTeams] Unloading the plugin...");
    }

    /**
     * Register listeners
     */
    private void registerListeners() {

        //Registers test listener
        Bukkit.getServer().getPluginManager().registerEvents(new TeamListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully loaded listeners");
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        final PaperCommandManager commandManager = new PaperCommandManager(this);

        //Registers teams command
        commandManager.registerCommand(new TeamCommand());
        commandManager.registerCommand(new TeamsCommand());

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Successfully loaded commands");
    }

}