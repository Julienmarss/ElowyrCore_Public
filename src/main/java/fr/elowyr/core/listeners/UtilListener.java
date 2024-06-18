package fr.elowyr.core.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UtilListener implements Listener {

    private final Map<EntityType, Material> removed_drops = new HashMap<>();
    private final Map<UUID, Long> last_message_timestamps = new ConcurrentHashMap<>();

    public UtilListener() {
        this.removed_drops.put(EntityType.SKELETON, Material.BOW);
        this.removed_drops.put(EntityType.PIG_ZOMBIE, Material.GOLD_SWORD);
        this.removed_drops.put(EntityType.IRON_GOLEM, Material.RED_ROSE);
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent e) {
        final Material material = this.removed_drops.get(e.getEntityType());
        if (material != null) {
            e.getDrops().removeIf(stack -> stack.getType().equals(material));
        }
    }
    
    @EventHandler
    public void onPlayerDrop(final PlayerDropItemEvent e) {
        final Item item = e.getItemDrop();
        if(e.getPlayer().hasPermission("elowyrcore.anti-enchants.bypass")) return;
        item.setItemStack(this.removeEnchants(item.getItemStack()));
    }
    
    @EventHandler
    public void onPlayerPickup(final PlayerPickupItemEvent e) {
        final Item item = e.getItem();
        if(e.getPlayer().hasPermission("elowyrcore.anti-enchants.bypass")) return;
        item.setItemStack(this.removeEnchants(item.getItemStack()));
    }
    
    @EventHandler
    public void onPlayerConsumeItem(final PlayerItemConsumeEvent e) {
        if(e.getPlayer().hasPermission("elowyrcore.anti-enchants.bypass")) return;
        e.setItem(this.removeEnchants(e.getItem()));
    }
    
    @EventHandler
    public void onPlayerItemDamage(final PlayerItemDamageEvent e) {
        if(e.getPlayer().hasPermission("elowyrcore.anti-enchants.bypass")) return;
        this.removeEnchants(e.getItem());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerEnchant(final EnchantItemEvent e) {
        e.getEnchantsToAdd().remove(Enchantment.KNOCKBACK);
        if (e.getEnchantsToAdd().isEmpty()) {
            e.setCancelled(true);
            e.setExpLevelCost(0);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (p.hasPermission("elowyrcore.anti-spam.bypass") || fPlayer.getChatMode() == ChatMode.FACTION) {
            return;
        }
        final Long lastMessage = this.last_message_timestamps.get(p.getUniqueId());
        final long now = System.currentTimeMillis();
        final long remaining = (lastMessage != null) ? (Config.get().getAntiSpamDelay() - (now - lastMessage)) : 0L;
        if (remaining > 0L) {
            e.setCancelled(true);
            if (remaining >= 1000L) {
                Lang.send(p, "listeners.anti-spam.seconds", "seconds", remaining / 1000L);
            }
            else {
                Lang.send(p, "listeners.anti-spam.milliseconds", "ms", remaining);
            }
        }
        else {
            this.last_message_timestamps.put(p.getUniqueId(), now);
        }
    }
    
    private ItemStack removeEnchants(final ItemStack stack) {
        stack.removeEnchantment(Enchantment.KNOCKBACK);
        return stack;
    }
}
