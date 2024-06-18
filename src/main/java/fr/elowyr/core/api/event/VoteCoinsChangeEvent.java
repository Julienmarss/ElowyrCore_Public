package fr.elowyr.core.api.event;

import fr.elowyr.core.user.data.User;
import org.bukkit.event.HandlerList;

public class VoteCoinsChangeEvent extends CancellableEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final User user;
    private final int oldVoteCoins;
    private final Action action;
    private int newVoteCoins;
    
    public VoteCoinsChangeEvent(final User user, final int oldVoteCoins, final int newVoteCoins, final Action action) {
        this.user = user;
        this.oldVoteCoins = oldVoteCoins;
        this.newVoteCoins = newVoteCoins;
        this.action = action;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public int getOldVoteCoins() {
        return this.oldVoteCoins;
    }
    
    public void setNewVoteCoins(final int newVoteCoins) {
        this.newVoteCoins = newVoteCoins;
    }
    
    public int getNewVoteCoins() {
        return this.newVoteCoins;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public HandlerList getHandlers() {
        return VoteCoinsChangeEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return VoteCoinsChangeEvent.handlers;
    }
    
    public enum Action
    {
        SET, 
        GIVE, 
        TAKE, 
        GIVEALL;
    }
}
