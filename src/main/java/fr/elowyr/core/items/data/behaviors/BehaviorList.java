package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.ElowyrCore;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BehaviorList
{
    private static final Map<String, Supplier<ItemBehavior>> BEHAVIORS;

    public BehaviorList() {
        BEHAVIORS.put("hammer", HammerBehavior::new);
    }

    public static ItemBehavior create(final String item, final String name) {
        if (name == null) {
            ElowyrCore.warn("Behavior name is null (item: " + item + ")");
            return null;
        }
        final Supplier<ItemBehavior> supplier = BehaviorList.BEHAVIORS.get(name);
        if (supplier == null) {
            ElowyrCore.warn("No behavior found for name '" + name + "' (item: " + name + ")");
            return null;
        }
        return supplier.get();
    }
    static {
        (BEHAVIORS = new HashMap<>()).put("hammer", HammerBehavior::new);
        BEHAVIORS.put("orbe", OrbeBehavior::new);
        BEHAVIORS.put("farm_armor", () -> FarmArmorBehavior.INSTANCE);
        BEHAVIORS.put("god_dager", GodDagerBehavior::new);
        BEHAVIORS.put("god_bow", GodBowBehavior::new);
        BEHAVIORS.put("scepter", TeleportationScepterBehavior::new);
        BEHAVIORS.put("placer",PlacerBehavior::new);
        BEHAVIORS.put("sell_stick", SellStickBehavior::new);
        BEHAVIORS.put("harvester", HarvesterBehavior::new);
        BEHAVIORS.put("hook", HookBehavior::new);
        BEHAVIORS.put("potioncounter", PotionCounterBehavior::new);
        BEHAVIORS.put("armorcounter", ArmorCounterBehavior::new);
        BEHAVIORS.put("farm_axe", FarmAxeBehavior::new);
        BEHAVIORS.put("farming_sword", FarmingSwordBehavior::new);
        BEHAVIORS.put("exp_pickaxe", ExpPickaxeBehavior::new);
    }
}
