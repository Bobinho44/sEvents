package fr.bobinho.sevents.utils.event;

import fr.bobinho.sevents.sEventsCore;
import fr.bobinho.sevents.utils.location.BLocationUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EventManager {

    private static final List<Event> events = new ArrayList<>();
    private static Event actualEvent;

    private static List<Event> getEvents() {
        return events;
    }

    public static Optional<Event> getEvent(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        return getEvents().stream().filter(event -> event.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static boolean isItAnEvent(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        return getEvent(name).isPresent();
    }

    public static void createAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(!isItAnEvent(event.getName()), "name is already used by an event");

        getEvents().add(event);
    }

    public static void deleteAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(isItAnEvent(event.getName()), "name is not used by an event");

        getEvents().remove(event);
    }

    public static boolean isInKOTHZone(@Nonnull Player player) {
        Validate.notNull(player, "player is null");

        return getEvents().stream().anyMatch(event -> event instanceof KOTH && ((KOTH) event).isInCaptureZone(player));
    }

    public static Event getActualEvent() {
        return actualEvent;
    }

    public static boolean anEventIsStarted() {
        return getActualEvent() != null;
    }

    public static void startAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(!anEventIsStarted(), "an event is already started");

        actualEvent = event;
        event.start();
    }

    public static void stopAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(anEventIsStarted(), "no event is started");

        actualEvent = null;
        event.stop();
    }

    public static void loadEvents() {
        YamlConfiguration configuration = sEventsCore.getEventsSetting().getConfiguration();

        //Loads events
        for (String eventType : configuration.getKeys(false)) {

            switch (eventType) {
                case "KOTH":

                    if (configuration.getConfigurationSection("KOTH.").getKeys(false) == null) {
                        break;
                    }

                    for (String eventName : Objects.requireNonNull(configuration.getConfigurationSection("KOTH.")).getKeys(false)) {

                        Location corner = BLocationUtil.getAsLocation(configuration.getString("KOTH." + eventName + ".corner", "world:0:0:0:0:0"));
                        Location oppositeCorner = BLocationUtil.getAsLocation(configuration.getString("KOTH." + eventName + ".oppositeCorner", "world:0:0:0:0:0"));
                        ItemStack[] lootTable = new ItemStack[KOTH.LOOT_TABLE_SIZE];
                        for (int i = 0; i < KOTH.LOOT_TABLE_SIZE; i++) {
                            lootTable[i] = configuration.getItemStack("KOTH." + eventName + ".lootTable." + i);
                        }

                        createAnEvent(new KOTH(eventName, corner, oppositeCorner, lootTable));
                    }
                    break;
            }
        }
    }

    public static void saveEvents() {
        YamlConfiguration configuration = sEventsCore.getEventsSetting().getConfiguration();
        sEventsCore.getEventsSetting().clear();

        //Saves events
        for (Event event : getEvents()) {
            switch (event.getClass().getSimpleName()) {
                case "KOTH":
                    configuration.set("KOTH." + event.getName() + ".corner", BLocationUtil.getAsString(((KOTH) event).getCorner()));
                    configuration.set("KOTH." + event.getName() + ".oppositeCorner", BLocationUtil.getAsString(((KOTH) event).getOppositeCorner()));
                    for (int i = 0; i < KOTH.LOOT_TABLE_SIZE; i++) {
                        configuration.set("KOTH." + event.getName() + ".lootTable." + i, ((KOTH) event).getLootTable()[i]);
                    }
                    break;
                default:
                    break;
            }
        }
        sEventsCore.getEventsSetting().save();
    }

}