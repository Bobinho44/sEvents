package fr.bobinho.sevents.utils.inventory.koth.holder;

import fr.bobinho.sevents.utils.event.KOTH;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class LootTableHolder implements InventoryHolder {

    private final KOTH koth;
    private final boolean readOnly;

    public LootTableHolder(@Nonnull KOTH koth, boolean readOnly) {
        Validate.notNull(koth, "koth is null");

        this.koth = koth;
        this.readOnly = readOnly;
    }

    public KOTH getKoth() {
        return koth;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

}