package fr.elowyr.core.api.event.votecoins;

import fr.elowyr.core.api.event.CancellableEvent;
import fr.elowyr.core.user.data.User;
import org.bukkit.event.HandlerList;

import java.util.List;

public class VoteCoinsGiveAllEvent extends CancellableEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final List<User> users;
    private int amount;
    
    public VoteCoinsGiveAllEvent(final List<User> users, final int amount) {
        this.users = users;
        this.amount = amount;
    }
    
    public List<User> getUsers() {
        return this.users;
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
        if (amount < 0) {
            this.setCancelled(true);
        }
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public HandlerList getHandlers() {
        return VoteCoinsGiveAllEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return VoteCoinsGiveAllEvent.handlers;
    }

}
