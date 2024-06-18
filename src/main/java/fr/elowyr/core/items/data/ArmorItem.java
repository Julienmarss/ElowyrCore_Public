package fr.elowyr.core.items.data;

import fr.elowyr.core.items.data.behaviors.ItemBehavior;
import org.bukkit.configuration.ConfigurationSection;

public class ArmorItem extends UsableItem {

    private final UsableItem helmet;
    private final UsableItem chestplate;
    private final UsableItem leggings;
    private final UsableItem boots;
    
    public ArmorItem(final String name, final ItemBehavior behavior, final ArmorPartItem helmet, final ArmorPartItem chestplate, final ArmorPartItem leggings, final ArmorPartItem boots) {
        super(name, null);
        this.behavior = behavior;
        this.helmet = helmet.armor(this);
        this.chestplate = chestplate.armor(this);
        this.leggings = leggings.armor(this);
        this.boots = boots.armor(this);
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        this.helmet.load(section.getConfigurationSection("helmet"));
        this.chestplate.load(section.getConfigurationSection("chestplate"));
        this.leggings.load(section.getConfigurationSection("leggings"));
        this.boots.load(section.getConfigurationSection("boots"));
        this.dropOnDeath = section.getBoolean("drop-on-death", true);
        this.behavior.load(section);
    }
}
