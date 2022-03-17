package fr.bobinho.sevents.listeners;

import fr.bobinho.sevents.utils.event.KOTH;
import fr.bobinho.sevents.utils.inventory.koth.holder.LootTableHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class KOTHListener implements Listener {

    /**
     * Listen when a player click on a loot table inventory
     *
     * @param e the inventory click event
     */
    @EventHandler
    public void onPlayerClickInInventory(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof LootTableHolder) {
            if (((LootTableHolder) e.getClickedInventory().getHolder()).isReadOnly()) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * Listen when a player close a loot table inventory
     *
     * @param e the inventory close event
     */
    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof LootTableHolder) {
            if (!((LootTableHolder) e.getInventory().getHolder()).isReadOnly()) {

                //Gets new loot table items
                ItemStack[] lootTable = new ItemStack[KOTH.LOOT_TABLE_SIZE];
                for (int i = 0; i < KOTH.LOOT_TABLE_SIZE; i++) {
                    lootTable[i] = e.getInventory().getItem(i);
                }

                //Sets new loot table
                ((LootTableHolder) e.getInventory().getHolder()).getKoth().setLootTable(lootTable);
            }
        }
    }

}