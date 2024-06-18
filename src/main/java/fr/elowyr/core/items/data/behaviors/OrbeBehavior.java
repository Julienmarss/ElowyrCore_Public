package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.utils.ConfigUtils;
import fr.elowyr.core.utils.CooldownUtils;
import fr.elowyr.core.utils.DurationFormatter;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class OrbeBehavior extends BasicBehavior {

    private int maxUses;
    private PotionEffect[] effects;

    public OrbeBehavior() {
        super(UseType.RIGHT_CLICK);
    }

    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.maxUses = section.getInt("maxUses", 3);
        this.effects = ConfigUtils.getSectionList(section, "effects").map(sub -> {
            PotionEffectType type = PotionEffectType.getByName(sub.getString("type"));
            int amplifier = sub.getInt("amplifier", 0);
            int seconds = sub.getInt("seconds", 60);
            boolean ambient = sub.getBoolean("ambient", true);
            boolean particles = sub.getBoolean("particles", true);
            return new PotionEffect(type, (20 * seconds), amplifier, ambient, particles);
        }).toArray(PotionEffect[]::new);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("uses", 0);
        data.setString("id", UUID.randomUUID().toString().substring(0, 5));
    }

    @Override
    public boolean use(ItemContext<?> ctx) {
        final Player player = ctx.getPlayer();
        if (CooldownUtils.isOnCooldown("orbe-" + ctx.getData().getString("id"), player)) {
            player.sendMessage(Utils.color("&6&lElowyr &7◆ &fVous devez attendre &d" +
                    DurationFormatter.getRemaining(CooldownUtils.getCooldownForPlayerLong("orbe-" + ctx.getData().getString("id"), player), false) + "&f."));
            return false;
        }

        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect, true);
        }
        CooldownUtils.addCooldown("orbe-" + ctx.getData().getString("id"), player, 360);
        if (ctx.getData().addInt("uses", 1) >= this.maxUses) {
            ctx.useItem(1);
            ctx.getData().setInt("uses", 0);
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 5, 5);
        }
        return false;
    }
}
