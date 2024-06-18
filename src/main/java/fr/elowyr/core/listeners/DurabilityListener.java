package fr.elowyr.core.listeners;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.List;
import java.util.Random;

public class DurabilityListener implements Listener {

    private final List<Material> countedItems = Lists.newArrayList(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);

    @EventHandler
    public void onItemDame(PlayerItemDamageEvent event) {
        final Random random = new Random();

        if (event.getItem() != null && this.countedItems.contains(event.getItem().getType()) && random.nextInt(2) == 1) {
            event.setCancelled(true);
        }
    }
}
