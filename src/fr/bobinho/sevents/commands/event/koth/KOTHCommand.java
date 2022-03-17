package fr.bobinho.sevents.commands.event.koth;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import fr.bobinho.sevents.utils.event.EventManager;
import fr.bobinho.sevents.utils.event.KOTH;
import fr.bobinho.sevents.utils.inventory.koth.LootTableInventory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("koth")
public class KOTHCommand extends BaseCommand {

    /**
     * Command koth admin
     *
     * @param commandSender the sender
     */
    @Syntax("/koth <name> setloot")
    @Default
    @CommandPermission("sevents.koth.admin")
    public void onTownEditCommand(CommandSender commandSender, @Single String name, @Single String action) {
        if (commandSender instanceof Player) {

            Player sender = (Player) commandSender;

            //Checks if the name is already use
            if (!EventManager.isItAnEvent(name)) {
                sender.sendMessage(ChatColor.RED + "The event " + name + " doesn't exist!");
                return;
            }

            //Sets table loot
            if (action.equalsIgnoreCase("setloot")) {

                sender.openInventory(LootTableInventory.getLootTableInventory((KOTH) EventManager.getEvent(name).get(), false));

            // Start the koth
            } else if (action.equalsIgnoreCase("start")) {

                //Checks if an event is already started
                if (EventManager.anEventIsStarted()) {
                    sender.sendMessage(ChatColor.RED + "An event is already started!");
                    return;
                }

                //Start the event
                EventManager.startAnEvent(EventManager.getEvent(name).get());
            }

            // Start the koth
            else if (action.equalsIgnoreCase("stop")) {

                //Checks if an event is already started
                if (!EventManager.anEventIsStarted()) {
                    sender.sendMessage(ChatColor.RED + "No event is launched!");
                    return;
                }

                if (!EventManager.getActualEvent().equals(EventManager.getEvent(name).get())) {
                    sender.sendMessage(ChatColor.RED + "The event " + name + " is not launched!");
                    return;
                }

                //Stops the event
                EventManager.stopAnEvent(EventManager.getEvent(name).get());
            }
        }
    }

    /**
     * Command koth loot
     *
     * @param commandSender the sender
     */
    @Syntax("/koth loot")
    @Subcommand("loot")
    @CommandPermission("sevents.koth.loot")
    public void onKOTHLootCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {

            Player sender = (Player) commandSender;

            //Checks if an event is already started
            if (!EventManager.anEventIsStarted()) {
                sender.sendMessage(ChatColor.RED + "No event is launched!");
                return;
            }

            if (!(EventManager.getActualEvent() instanceof KOTH)) {
                sender.sendMessage(ChatColor.RED + "The launched event is not a KOTH!");
                return;
            }

            sender.openInventory(LootTableInventory.getLootTableInventory((KOTH) EventManager.getActualEvent(), true));
        }
    }

    /**
     * Command koth create
     *
     * @param commandSender the sender
     */
    @Syntax("/koth create <name>")
    @Subcommand("create")
    @CommandPermission("sevents.koth.create")
    public void onKOTHLootCommand(CommandSender commandSender, @Single String name) {
        if (commandSender instanceof Player) {

            Player sender = (Player) commandSender;

            //Checks if an event has this name
            if (EventManager.isItAnEvent(name)) {
                sender.sendMessage(ChatColor.RED + name + " is already used!");
                return;
            }

            //Gets player selection
            LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(sender));
            RegionSelector selector = session.getRegionSelector(BukkitAdapter.adapt(sender.getWorld()));

            try {

                //Gets player selection points
                BlockVector3 maximumPoint = selector.getRegion().getMaximumPoint();
                BlockVector3 minimumPoint = selector.getRegion().getMinimumPoint();
                Location corner = new Location(sender.getWorld(), maximumPoint.getX() + 1, maximumPoint.getY(), maximumPoint.getZ() + 1);
                Location oppositeCorner = new Location(sender.getWorld(), minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ());

                //Creates the koth
                EventManager.createAnEvent(new KOTH(name, corner, oppositeCorner));

                //Sends message
                sender.sendMessage(ChatColor.GREEN + "You have created the KOTH " + name + ".");

            } catch (IncompleteRegionException e) {

                //Sends no selection error message
                sender.sendMessage(ChatColor.RED + "You do not have a selected region!!");
            }
        }
    }

    /**
     * Command koth delete
     *
     * @param commandSender the sender
     */
    @Syntax("/koth delete <name>")
    @Subcommand("delete")
    @CommandPermission("sevents.koth.delete")
    public void onKOTHDeleteCommand(CommandSender commandSender, @Single String name) {
        if (commandSender instanceof Player) {

            Player sender = (Player) commandSender;

            //Checks if an event has this name
            if (!EventManager.isItAnEvent(name)) {
                sender.sendMessage(ChatColor.RED + name + " is not an event name!");
                return;
            }

            //Checks if a KOTH event has this name
            if (!(EventManager.getEvent(name).get() instanceof KOTH)) {
                sender.sendMessage(ChatColor.RED + name + " is not a KOTH event name!");
                return;
            }

            //Creates the koth
            EventManager.deleteAnEvent(EventManager.getEvent(name).get());

            //Sends message
            sender.sendMessage(ChatColor.GREEN + "You have deleted the KOTH " + name + ".");
        }
    }

}