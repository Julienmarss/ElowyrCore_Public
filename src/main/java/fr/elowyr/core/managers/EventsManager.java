package fr.elowyr.core.managers;

import fr.elowyr.core.data.IEvent;
import fr.elowyr.core.data.events.Largage;

import java.util.HashMap;
import java.util.Optional;

public class EventsManager {

    private static EventsManager instance;
    private HashMap<String, IEvent> tEvents;

    public EventsManager() {
        instance = this;
        this.tEvents = new HashMap<>();

        this.registerEvent(new Largage());
    }

    private void registerEvent(IEvent iEvent) {
        this.tEvents.put(iEvent.getName(), iEvent);
    }

    public Optional<IEvent> getEvent(String eventName) {
        return Optional.ofNullable(this.tEvents.get(eventName));
    }

    public static EventsManager getInstance() {
        return instance;
    }

    public static void setInstance(EventsManager instance) {
        EventsManager.instance = instance;
    }

    public HashMap<String, IEvent> getTEvents() {
        return tEvents;
    }

    public void settEvents(HashMap<String, IEvent> tEvents) {
        this.tEvents = tEvents;
    }
}
