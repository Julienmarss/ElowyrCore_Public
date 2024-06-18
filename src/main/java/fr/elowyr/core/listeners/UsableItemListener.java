package fr.elowyr.core.listeners;

import fr.elowyr.core.api.event.PlayerDropOnDeathEvent;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.behaviors.UseType;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.utils.ShopGuiPlusHook;
import net.brcdev.shopgui.event.ShopPreTransactionEvent;
import net.brcdev.shopgui.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class UsableItemListener implements Listener {

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        ItemStack stack = event.getItemDrop().getItemStack();
        UsableItem item = ItemsManager.getInstance().getByItemStack(NMS.getNBT(stack));
        if (item != null && !item.canDrop())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        useItem(event, event.getPlayer(), UseType.ofInteract(event));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        useItem(event, event.getPlayer(), UseType.BLOCK_BREAK);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        useItem(event, event.getPlayer(), UseType.BLOCK_PLACE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity))
            return;
        useItem(event, (Player) event.getDamager(), UseType.ENTITY_DAMAGE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        useItem(event, event.getPlayer(), UseType.INTERACT_PLAYER);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null)
            useItem(event, killer, UseType.PLAYER_KILL);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent event) {
        final Player player = event.getPlayer();
        useItem(event, player, UseType.FISHING);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShopPreTransaction(final ShopPreTransactionEvent e) {
        if (e.getShopAction() != ShopManager.ShopAction.BUY) {
            final Player p = e.getPlayer();
            final double multiplier = ShopGuiPlusHook.getShopMultiplier(p);
            e.setPrice(e.getPrice() * multiplier);
        }
    }

    @EventHandler
    public void onPlayerDropOnDeath(PlayerDropOnDeathEvent event) {
        List<ItemStack> drops = event.getAllDrops();
        for (ItemStack drop : drops) {
            NBTData data = NMS.getNBT(drop);
            UsableItem item = ItemsManager.getInstance().getByItemStack(data);
            if (item != null && !item.doesDropOnDeath())
                event.keepItem(drop);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && Manager.FARM_ARMOR_EQUIPPED.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        LivingEntity entity = e.getEntity();
        UUID killerId = Manager.KILLERS.remove(entity.getUniqueId());
        if (killerId == null) return;
        Player killer = Bukkit.getPlayer(killerId);
        if (killer != null) {
            for (ItemStack drop : e.getDrops()) {
                if (drop.getType() == Material.ENDER_PEARL || drop.getType() == Material.GOLD_INGOT) return;
            }
            ShopGuiPlusHook.sellItems(killer, e.getDrops(), "listeners.entity-killed");
        }
        useItem(e, e.getEntity().getKiller(), UseType.ENTITY_KILL);
    }

    private <E extends Event> void useItem(E event, Player player, UseType useType) {
        useItem(event, player, player.getItemInHand(), useType);
    }

    private <E extends Event> void useItem(E event, Player player, ItemStack stack, UseType useType) {
        if (stack == null || stack.getType().equals(Material.AIR))
            return;
        NBTData data = NMS.getNBT(stack);
        UsableItem item = ItemsManager.getInstance().getByItemStack(data);
        if (item == null)
            return;
        ItemContext<E> ctx = new ItemContext(event, player, stack, data, item, useType);
        item.use(ctx);
    }
}
