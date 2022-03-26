package fr.bobinho.sevents.utils.inventory.koth;

import fr.bobinho.sevents.utils.event.KOTH;
import fr.bobinho.sevents.utils.inventory.koth.holder.LootTableHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public class LootTableInventory {


    @Nonnull
    public static Inventory getLootTableInventory(KOTH koth, boolean readOnly) {
        Inventory inventory = Bukkit.createInventory(new LootTableHolder(koth, readOnly), KOTH.LOOT_TABLE_SIZE, Component.text(koth.getName() + "'s loot table"));

        //Places initial items
        for (int i = 0; i < KOTH.LOOT_TABLE_SIZE; i++) {
            inventory.setItem(i, koth.getLootTable()[i]);
        }

        //Returns the inventory
        return inventory;
    }

}