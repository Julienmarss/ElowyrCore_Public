package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FarmArmorBehavior extends BasicBehavior
{
    public static final FarmArmorBehavior INSTANCE = new FarmArmorBehavior();
    private PotionEffect[] effects;
    
    private FarmArmorBehavior() {
        super();
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        this.effects = ConfigUtils.getSectionList(section, "effects").map(sub -> {
            PotionEffectType type = PotionEffectType.getByName(sub.getString("type"));
            int amplifier = sub.getInt("amplifier", 0);
            boolean ambient = sub.getBoolean("ambient", true);
            boolean particles = sub.getBoolean("particles", true);
            return new PotionEffect(type, Integer.MAX_VALUE, amplifier, ambient, particles);
        }).toArray(PotionEffect[]::new);
    }
    
    @Override
    public void getDefaultData(final NBTData data) {
    }
    
    @Override
    public boolean use(final ItemContext<?> ctx) {
        return false;
    }
    
    public void addEffects(final Player player) {
        for (final PotionEffect effect : this.effects) {
            player.addPotionEffect(effect);
        }
    }
    
    public void removeEffects(final Player player) {
        for (final PotionEffect effect : this.effects) {
            player.removePotionEffect(effect.getType());
        }
    }
}
