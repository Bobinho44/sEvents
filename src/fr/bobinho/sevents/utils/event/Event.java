package fr.bobinho.sevents.utils.event;

public abstract class Event {

    private final String name;

    protected Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void start();

    public abstract void stop();
}
