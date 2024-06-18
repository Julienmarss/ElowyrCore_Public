package fr.elowyr.core.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class InstantKillEvent extends CancellableEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final Player killer;
    private final LivingEntity entity;
    
    public InstantKillEvent(final Player killer, final LivingEntity entity) {
        this.killer = killer;
        this.entity = entity;
    }
    
    public Player getKiller() {
        return this.killer;
    }
    
    public LivingEntity getEntity() {
        return this.entity;
    }
    
    public HandlerList getHandlers() {
        return InstantKillEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return InstantKillEvent.handlers;
    }

}
