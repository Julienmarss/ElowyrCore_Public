package fr.elowyr.core.items.data.behaviors;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public enum UseType
{
    NONE, 
    BLOCK_BREAK, 
    BLOCK_PLACE, 
    PHYSICAL, 
    RIGHT_CLICK, 
    RIGHT_CLICK_AIR, 
    RIGHT_CLICK_BLOCK, 
    LEFT_CLICK, 
    LEFT_CLICK_AIR, 
    LEFT_CLICK_BLOCK, 
    ENTITY_DAMAGE,
    FISHING,
    PLAYER_KILL,
    ENTITY_KILL,
    INTERACT_PLAYER;
    
    public boolean isCompatible(final UseType other) {
        if (this == UseType.LEFT_CLICK) {
            return other == UseType.LEFT_CLICK || other == UseType.LEFT_CLICK_AIR || other == UseType.LEFT_CLICK_BLOCK;
        }
        if (this == UseType.RIGHT_CLICK) {
            return other == UseType.RIGHT_CLICK || other == UseType.RIGHT_CLICK_AIR || other == UseType.RIGHT_CLICK_BLOCK;
        }
        return this == other;
    }
    
    public static UseType ofInteract(final PlayerInteractEvent e) {
        final Action action = e.getAction();
        switch (action) {
            case LEFT_CLICK_AIR: {
                return UseType.LEFT_CLICK_AIR;
            }
            case LEFT_CLICK_BLOCK: {
                return UseType.LEFT_CLICK_BLOCK;
            }
            case RIGHT_CLICK_AIR: {
                return UseType.RIGHT_CLICK_AIR;
            }
            case RIGHT_CLICK_BLOCK: {
                return UseType.RIGHT_CLICK_BLOCK;
            }
            case PHYSICAL: {
                return UseType.PHYSICAL;
            }
            default: {
                return UseType.NONE;
            }
        }
    }
}
