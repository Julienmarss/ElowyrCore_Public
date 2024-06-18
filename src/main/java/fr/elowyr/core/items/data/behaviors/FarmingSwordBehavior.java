package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.api.event.InstantKillEvent;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FarmingSwordBehavior extends BasicBehavior {

    public FarmingSwordBehavior() {
        super(UseType.ENTITY_DAMAGE);
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("kill", 0);
    }

    @Override
    public boolean use(ItemContext<?> ctx) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) ctx.getEvent();
        LivingEntity entity = (LivingEntity) e.getEntity();
        Player killer = ctx.getPlayer();

        if (entity instanceof Player) return false;
        if (entity.getCustomName() != null && entity.getCustomName().startsWith(Utils.color("&r&l&r&6&lBoss &7◆"))) return false;

        if ((new InstantKillEvent(killer, entity)).run()) return false;

        Manager.KILLERS.put(entity.getUniqueId(), killer.getUniqueId());
        entity.setHealth(0.0D);
        ctx.getData().addInt("kill", 1);
        return false;
    }
}
