package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import org.bukkit.configuration.ConfigurationSection;

public interface ItemBehavior
{
    void load(final ConfigurationSection section);
    
    void getDefaultData(final NBTData data);
    
    UseType[] getUseTypes();
    
    boolean use(final ItemContext<?> context);
}
