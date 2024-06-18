package net.elowyr.common;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class HoeUseEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private List<Block> drops;

    public HoeUseEvent(Player who, List<Block> drops) {
        super(who);
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public List<Block> getDrops() {
        return drops;
    }
}
