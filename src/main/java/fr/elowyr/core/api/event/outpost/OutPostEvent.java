package fr.elowyr.core.api.event.outpost;

import fr.elowyr.core.api.event.CancellableEvent;
import fr.elowyr.core.data.events.OutPost;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class OutPostEvent extends CancellableEvent {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSender sender;

    private final OutPost outPost;

    private final Action action;

    public OutPostEvent(CommandSender sender, OutPost outPost, Action action) {
        this.sender = sender;
        this.outPost = outPost;
        this.action = action;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public OutPost getOutPost() {
        return this.outPost;
    }

    public Action getAction() {
        return this.action;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Action {
        CREATE, DELETE;
    }
}