package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;

public class GodBowBehavior extends BasicBehavior {

    public GodBowBehavior() {
        super(UseType.FISHING);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("hits", 0);
    }

    @Override
    public boolean use(ItemContext<?> ctx) {
        ctx.getData().addInt("hits", 1);
        return false;
    }
}
