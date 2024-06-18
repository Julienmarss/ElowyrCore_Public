package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.ItemsManager;
import org.bukkit.configuration.ConfigurationSection;

public abstract class UpgradableBehavior extends BasicBehavior {

    protected int max;
    protected boolean isUpgradable;
    protected UsableItem upgradeItem;
    
    public UpgradableBehavior(final UseType... types) {
        super(types);
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.isUpgradable = section.contains("upgrade");
        if (this.isUpgradable) {
            this.max = section.getInt("upgrade.max");
            this.upgradeItem = ItemsManager.getInstance().getByName(section.getString("upgrade.item"));
        }
    }
}
