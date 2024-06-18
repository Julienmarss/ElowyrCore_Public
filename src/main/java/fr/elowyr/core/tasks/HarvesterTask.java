package fr.elowyr.core.tasks;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HarvesterTask implements Runnable {

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getType() == Material.DIAMOND_HOE
                    && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.color("&e&l✦ &6&lHoue de l'Empereur &e&l✦"))) {
                NBTData data = NMS.getNBT(player.getItemInHand());
                if (data == null) return;
                if (data.getBoolean("haste")) {
                    if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                        Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 5, 0)));
                    }
                }
                if (data.getInt("speed") == 1) {
                    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                        Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 0)));
                    }
                } else if (data.getInt("speed") == 2) {
                        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                            Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1)));
                        }
                }
            }
        });
    }
}
