package fr.elowyr.core.items.data;

import fr.elowyr.core.items.data.behaviors.BehaviorList;
import fr.elowyr.core.items.data.behaviors.ItemBehavior;
import fr.elowyr.core.items.data.behaviors.UseType;
import fr.elowyr.core.items.data.nbt.NBTData;
import org.bukkit.configuration.ConfigurationSection;

public class UsableItem extends Item
{
    protected ItemBehavior behavior;
    protected boolean dropOnDeath;
    private boolean canDrop;
    private Double shopMultiplier;
    private boolean active;

    public UsableItem(final String name, final String behaviorName) {
        super(name);
        this.behavior = BehaviorList.create(name, behaviorName);
        this.dropOnDeath = true;
        this.active = true;
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.dropOnDeath = section.getBoolean("drop-on-death", true);
        this.canDrop = section.getBoolean("can-drop", true);
        this.shopMultiplier = (section.contains("shop-multiplier") ? section.getDouble("shop-multiplier") : null);
        if (this.behavior != null) {
            this.behavior.load(section);
        }
    }
    
    @Override
    public NBTData getDefaultData(final String owner) {
        final NBTData data = super.getDefaultData(owner);
        if (this.behavior != null) {
            this.behavior.getDefaultData(data);
        }
        return data;
    }
    
    public ItemBehavior getBehavior() {
        return this.behavior;
    }
    
    private boolean isCompatible(final ItemContext<?> ctx) {
        if (this.behavior == null) {
            return false;
        }
        for (final UseType useType : this.behavior.getUseTypes()) {
            if (useType.isCompatible(ctx.getUseType())) {
                return true;
            }
        }
        return false;
    }
    
    public void use(final ItemContext<?> ctx) {
        final ItemBehavior behavior = this.getBehavior();
        if (!this.isCompatible(ctx)) {
            return;
        }
        if (behavior != null) {
            behavior.use(ctx);
        }
        if (ctx.isChanged()) {
            ctx.updateHand();
        }
    }
    
    public boolean doesDropOnDeath() {
        return this.dropOnDeath;
    }
    
    public boolean canDrop() {
        return this.canDrop;
    }
    
    public Double getShopMultiplier() {
        return this.shopMultiplier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
