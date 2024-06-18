package fr.elowyr.core.tasks;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.behaviors.FarmArmorBehavior;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.managers.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class FarmArmorTask extends BukkitRunnable {
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final boolean full = this.hasFullArmor(player.getInventory());
            final boolean oldFull = Manager.FARM_ARMOR_EQUIPPED.contains(player.getUniqueId());
            if (oldFull && !full) {
                Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> {
                    FarmArmorBehavior.INSTANCE.removeEffects(player);
                    Manager.FARM_ARMOR_EQUIPPED.remove(player.getUniqueId());
                });
            } else {
                if (oldFull || !full) {
                    continue;
                }
                Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> {
                    FarmArmorBehavior.INSTANCE.addEffects(player);
                    Manager.FARM_ARMOR_EQUIPPED.add(player.getUniqueId());
                });
            }
        }
    }

    private boolean hasFullArmor(final PlayerInventory inv) {
        for (final ItemStack armor : inv.getArmorContents()) {
            if (armor == null || armor.getType().equals(Material.AIR)) {
                return false;
            }
            final UsableItem item = ItemsManager.getInstance().getByItemStack(NMS.getNBT(armor));
            if (item != ItemsManager.getInstance().getFARM_HELMET() && item != ItemsManager.getInstance().getFARM_CHESTPLATE() && item != ItemsManager.getInstance().getFARM_LEGGINGS() && item != ItemsManager.getInstance().getFARM_BOOTS()) {
                return false;
            }
        }
        return true;
    }
}
