package fr.elowyr.core.events;

import fr.elowyr.core.data.IEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventStartEvent extends Event implements Cancellable {

    private IEvent event;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public EventStartEvent(IEvent event) {
        this.event = event;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public IEvent getEvent() {
        return event;
    }

    public void setEvent(IEvent event) {
        this.event = event;
    }
}
