package fr.elowyr.core.api.event;

import org.bukkit.event.Cancellable;

public abstract class CancellableEvent extends BasicEvent implements Cancellable
{
    private boolean cancelled;
    
    public CancellableEvent() {
        this.cancelled = false;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public boolean run() {
        super.run();
        return this.cancelled;
    }
}
