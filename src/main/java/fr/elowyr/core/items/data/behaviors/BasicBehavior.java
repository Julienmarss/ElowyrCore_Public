package fr.elowyr.core.items.data.behaviors;

import org.bukkit.configuration.ConfigurationSection;

public abstract class BasicBehavior implements ItemBehavior
{
    private final UseType[] useTypes;
    
    public BasicBehavior(final UseType... types) {
        this.useTypes = types;
    }
    
    @Override
    public UseType[] getUseTypes() {
        return this.useTypes;
    }
    
    @Override
    public void load(final ConfigurationSection section) {
    }
}
