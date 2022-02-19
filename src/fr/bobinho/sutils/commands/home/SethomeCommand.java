package fr.bobinho.sutils.commands.home;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.bobinho.sutils.utils.home.sUtilsHomeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("sethome")
public class SethomeCommand extends BaseCommand {

    /**
     * Command sethome
     *
     * @param commandSender the sender
     */
    @Default
    @Syntax("/sethome")
    @CommandPermission("sutils.sethome")
    public void onSethomeCommand(CommandSender commandSender, @Single String homeName) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            //Checks if the home exist
            if (sUtilsHomeManager.isItsUtilsHome(sender, homeName)) {
                sender.sendMessage(ChatColor.RED + "The home " + homeName + " already exists!");
                return;
            }

            //Checks if the player have the maximal home number
            if (sUtilsHomeManager.getNumberOfsUtilsHomes(sender) >= sUtilsHomeManager.getNumberOfsUtilsHomesAllowed(sender)) {
                sender.sendMessage(ChatColor.RED + "You have already reached the maximum number of homes!");
                return;
            }

            //Creates the home
            sUtilsHomeManager.createsUtilsHome(sender, homeName);

            //Sends message
            sender.sendMessage(ChatColor.GREEN + "You have created the home " + homeName + ".");
        }
    }

}