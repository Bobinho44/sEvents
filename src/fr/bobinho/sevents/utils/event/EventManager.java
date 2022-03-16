package fr.bobinho.sevents.utils.event;

import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventManager {

    private static final List<Event> events = new ArrayList<>();
    private static Event actualEvent;

    private static List<Event> getEvents() {
        return events;
    }

    public Optional<Event> getEvent(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        return getEvents().stream().filter(event -> event.getName().equalsIgnoreCase(name)).findFirst();
    }

    public boolean isItAnEvent(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        return getEvent(name).isPresent();
    }

    public void createAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(!isItAnEvent(event.getName()), "name is already used by an event");

        getEvents().add(event);
    }

    public void deleteAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(isItAnEvent(event.getName()), "name is not used by an event");

        getEvents().remove(event);
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
    }

    public static void stopAnEvent(@Nonnull Event event) {
        Validate.notNull(event, "event is null");
        Validate.isTrue(anEventIsStarted(), "no event is started");

        actualEvent = null;
    }

}