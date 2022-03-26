package fr.bobinho.sevents.utils.event;

import fr.bobinho.sevents.sEventsCore;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum MessageKOTH {
    START_KOTH,
    NO_WINNER_KOTH,
    WINNER_KOTH;

    public String getMessage(@Nonnull String name, @Nonnull String koth) {
        Validate.notNull(name, "name is null");
        Validate.notNull(koth, "koth is null");

        return ChatColor.translateAlternateColorCodes('&', sEventsCore.getMessagesSetting().getConfiguration().getString(this.toString(), "N/D"))
                .replaceAll("%player%", name)
                .replaceAll("%koth%", koth);
    }

    public String getMessage(@Nonnull String koth) {
        Validate.notNull(koth, "koth is null");

        return getMessage("", koth);
    }

}
