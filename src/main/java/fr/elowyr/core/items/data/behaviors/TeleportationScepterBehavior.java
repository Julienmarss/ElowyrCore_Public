package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.CooldownUtils;
import fr.elowyr.core.utils.TimeUtils;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.Tag;
import net.minelink.ctplus.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportationScepterBehavior extends BasicBehavior {

    private int uses;
    private int delay;
    private int cooldown;
    private final TagManager tagManager = JavaPlugin.getPlugin(CombatTagPlus.class).getTagManager();

    public TeleportationScepterBehavior() {
        super(UseType.RIGHT_CLICK);
    }

    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.uses = section.getInt("uses", 3);
        this.delay = section.getInt("delay", 3);
        this.cooldown = section.getInt("cooldown", 5);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("uses", uses);
    }

    @Override
    public boolean use(ItemContext<?> ctx) {
        final Player player = ctx.getPlayer();
        final Tag tag = tagManager.getTag(player.getUniqueId());
        if (tag == null || tag.isExpired()) {
            Lang.send(player, "items.scepter.not-combat");
            return false;
        }
        if(tag.getAttackerName() == null) return false;
        if (CooldownUtils.isOnCooldown("sceptre", player)) {
            Lang.send(player, "items.scepter.cooldown", "time", TimeUtils.format(CooldownUtils.getCooldownForPlayerLong("sceptre", player)));
            return false;
        }
        CooldownUtils.addCooldown("sceptre", player, cooldown);
        new BukkitRunnable() {
            int seconds = delay;
            @Override
            public void run() {
                if (seconds == 0) {
                    player.teleport(Bukkit.getPlayer(tag.getVictimName()).getLocation());
                    this.cancel();
                    return;
                }
                Lang.send(player, "items.scepter.teleport-delay", "time", seconds);
                seconds--;
            }
        }.runTaskTimer(ElowyrCore.getInstance(), 0, 20);
        if (ctx.getData().removeInt("uses", 1) == 0) {
            ctx.useItem(1);
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 5, 5);
        }
        return false;
    }
}
