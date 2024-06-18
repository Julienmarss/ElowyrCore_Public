package fr.elowyr.core.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayerDropOnDeathEvent extends BasicEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final List<ItemStack> drops;

    private final List<ItemStack> armor;

    private final List<ItemStack> keep;

    public PlayerDropOnDeathEvent(Player player, List<ItemStack> drops, List<ItemStack> armor) {
        this.player = player;
        this.drops = drops;
        this.armor = armor;
        this.keep = new LinkedList<>();
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }

    public List<ItemStack> getArmor() {
        return this.armor;
    }

    public List<ItemStack> getAllDrops() {
        List<ItemStack> items = new ArrayList<>(this.drops.size() + this.armor.size());
        items.addAll(this.drops);
        items.addAll(this.armor);
        return items;
    }

    public void keepItem(ItemStack stack) {
        this.keep.add(stack);
        this.drops.remove(stack);
        this.armor.remove(stack);
    }

    public List<ItemStack> getKeep() {
        return this.keep;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
