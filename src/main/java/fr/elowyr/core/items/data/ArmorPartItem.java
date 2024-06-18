package fr.elowyr.core.items.data;


import fr.elowyr.core.items.data.behaviors.ItemBehavior;

public class ArmorPartItem extends UsableItem {

    private ArmorItem armor;
    
    public ArmorPartItem(final String name) {
        super(name, null);
    }
    
    ArmorPartItem armor(final ArmorItem armor) {
        this.armor = armor;
        return this;
    }
    
    @Override
    public ItemBehavior getBehavior() {
        return this.armor.getBehavior();
    }
}
