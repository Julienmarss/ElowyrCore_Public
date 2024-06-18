package fr.elowyr.core.items.data;

import fr.elowyr.core.items.data.behaviors.UseType;
import fr.elowyr.core.items.data.nbt.NBTData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class ItemContext<E extends Event> {

    private static final ItemStack EMPTY = new ItemStack(Material.AIR);
    private final E event;
    private final Player player;
    private ItemStack itemStack;
    private final UseType useType;
    private UsableItem item;
    private NBTData data;
    private int amount;
    private short durability;
    private boolean changed;
    
    public ItemContext(final E event, final Player player, final ItemStack itemStack, final NBTData data, final UsableItem item, final UseType useType) {
        this.event = event;
        this.player = player;
        this.itemStack = itemStack;
        this.data = data;
        this.item = item;
        this.useType = useType;
        this.amount = itemStack.getAmount();
        this.durability = itemStack.getDurability();
        this.changed = false;
    }
    
    public E getEvent() {
        return this.event;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    public UsableItem getItem() {
        return this.item;
    }
    
    public NBTData getData() {
        return this.data;
    }
    
    public UseType getUseType() {
        return this.useType;
    }
    
    public void setItem(final UsableItem item, final NBTData data) {
        this.item = item;
        this.data = data;
        this.changed = true;
    }
    
    public void damage(final short damage) {
        if (this.itemStack == null) {
            return;
        }
        this.durability += damage;
        this.changed = true;
        if (this.durability >= this.itemStack.getType().getMaxDurability()) {
            this.itemStack = null;
            this.durability = 0;
        }
    }
    
    public void cancel() {
        if (this.event instanceof Cancellable) {
            ((Cancellable)this.event).setCancelled(true);
        }
    }
    
    public boolean isChanged() {
        return this.changed || this.data.isChanged();
    }
    
    public void useItem(final int amount) {
        this.amount -= amount;
        this.changed = true;
        if (this.amount <= 0) {
            this.itemStack = null;
        }
    }
    
    public void updateHand() {
        if (this.itemStack != null && this.amount > 0) {
            this.itemStack = this.item.build(this.data, this.amount, this.durability);
            this.player.setItemInHand(this.itemStack);
        }
        else {
            this.player.setItemInHand(ItemContext.EMPTY);
        }
        this.player.updateInventory();
    }
}
