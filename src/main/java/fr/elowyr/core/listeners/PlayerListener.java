package fr.elowyr.core.listeners;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.google.common.collect.Sets;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.api.event.PlayerDropOnDeathEvent;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.data.Boost;
import fr.elowyr.core.data.BoostType;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.items.data.Item;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.managers.*;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.InventoryUtils;
import fr.elowyr.core.utils.ItemBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.imanity.imanityspigot.event.AsyncTabCompleteEvent;

import java.util.*;
import java.util.stream.Stream;

public class PlayerListener implements Listener {

    private final Set<String> whitelist = Sets.newHashSet("video", "live", "msg", "r");
    private final Short[] shorts = { 8229, 8226, 8265, 8257, 8259 };

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTabComplete(AsyncTabCompleteEvent e) {
        if (!(e.getSender() instanceof Player))
            return;
        Stream<String> playerNames = Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName);
        Collection<String> tab = e.getCompletions();
        String message = e.getBuffer();
        int index = message.lastIndexOf(' ');
        String lastToken = (index < 0) ? message : message.substring(index + 1);
        if (lastToken.startsWith("/") && lastToken.length() < 3) {
            playerNames.forEach(tab::remove);
            if (tab.isEmpty()) {
                e.setCancelled(true);
                Lang.send(e.getSender(), "listeners.empty-tab");
            }
        } else {
            playerNames.filter(name -> name.startsWith(lastToken)).forEach(tab::add);
        }
        /*if (lastToken.length() < 2) {
            playerNames.forEach(tab::remove);
            if (tab.isEmpty()) {
                e.setCancelled(true);
                Lang.send(e.getSender(), "listeners.empty-tab");
            }
        } else {
            playerNames.filter(name -> name.startsWith(lastToken)).forEach(tab::add);
        }*/
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().name().contains("ORE")) {
            block.setMetadata("placed", new FixedMetadataValue(ElowyrCore.getInstance(), "placed"));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tabcomplete.use")) {
            return;
        }
        String message = event.getMessage();

        final String cmd = message.split(" ")[0].toLowerCase().substring(1);

        if (ElowyrCore.getInstance().getCommands().contains(cmd)) {
            event.setCancelled(true);
            Lang.send(player, "listeners.command-blocked");
            return;
        }
        if (!player.hasPermission("specialchars.use") && message.contains(":") && !message.toLowerCase().startsWith("/f desc") && !this.whitelist.contains(cmd)) {
            event.setCancelled(true);
            Lang.send(player, "listeners.specialchars");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(PlayerChatTabCompleteEvent event) {

        if (event.getPlayer().hasPermission("tabcomplete.use")) {
            return;
        }

        for (String tabCompletion : new ArrayList<>(event.getTabCompletions())) {
            event.getTabCompletions().remove(tabCompletion);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (block == null || block.getType() != Material.SOIL)
                return;
            Material up = block.getRelative(BlockFace.UP).getType();
            if (up == Material.CROPS || up == Material.CARROT || up == Material.POTATO || up == Material.PUMPKIN_STEM || up == Material.MELON_STEM) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                block.setTypeIdAndData(block.getType().getId(), block.getData(), true);
            }
        }
    }

    @EventHandler
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        UserManager.getOrLoad(e.getUniqueId(), user -> {
            if (user == null) {
                user = new User(e.getUniqueId(), e.getName());
                UserManager.addUser(user);
                DbUtils.insertUser(user);
            }
            else if (!user.getUsername().equals(e.getName())) {
                user.setUsername(e.getName());
                DbUtils.updateUser(user, "username", e.getName());
                GlobalClassificationManager.get().setUsername(e.getUniqueId(), e.getName());
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = UserManager.getByUniqueId(player.getUniqueId());
        if (user != null) {
            UserManager.setOnline(user, player, true);
        } else {
            Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> player.kickPlayer(Lang.get().getString("listeners.starting")));
        }
        if (!player.hasPlayedBefore()) {
            final Item item = ItemsManager.getInstance().getByName("harvester");
            InventoryUtils.addItem(player, item.build(player.getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        User user = UserManager.get(p.getUniqueId());
        if (user != null) {
            UserManager.setOnline(user, null, false);
            user.save(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        Player p = e.getEntity();
        User user = UserManager.get(p.getUniqueId());
        Player killer = p.getKiller();
        int ksKill = Config.get().getKillKsBc();
        if (user == null)
            return;
        if (user.getSerialKill() >= ksKill)
            Lang.broadcast("listeners.kill-streak.death", "name", p.getName(), "kill", user.getSerialKill());
        if (killer != null) {
            User killerUser = UserManager.get(killer.getUniqueId());
            if (killerUser == null)
                return;
            user.setSerialKill(0);
            killerUser.setSerialKill(killerUser.getSerialKill() + 1);
            DbUtils.updateUser(user, "serial_kill", 0);
            DbUtils.updateUser(killerUser, "serial_kill", killerUser.getSerialKill());
            if (ksKill > 0 && killerUser.getSerialKill() % ksKill == 0)
                Lang.broadcast("listeners.kill-streak.kill", "name", killer.getName(), "kill", killerUser.getSerialKill());

            if (e.getEntityType() == EntityType.PLAYER || e.getEntity().getKiller().getType() == EntityType.PLAYER) {
                Lang.broadcast("listeners.death", "victim", e.getEntity().getName(),
                        "item", killer.getEquipment().getItemInHand().getItemMeta().getDisplayName(), "killer", killer.getName());
            }
        } else {
            Lang.broadcast("listeners.deathOther", "victim", e.getEntity().getName());
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerDropOnDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PlayerInventory inv = p.getInventory();
        Location loc = p.getLocation().clone();
        List<ItemStack> armorContent = Arrays.asList(inv.getContents());
        List<ItemStack> drops = new LinkedList<>(e.getDrops());
        LinkedList<ItemStack> armor = new LinkedList<>();
        Iterator iter = drops.iterator();
        while (iter.hasNext()) {
            ItemStack drop = (ItemStack)iter.next();
            if (!armorContent.contains(drop)) continue;
            iter.remove();
            armor.add(drop);
        }
        e.getDrops().clear();
        PlayerDropOnDeathEvent event = new PlayerDropOnDeathEvent(p, drops, armor);
        event.run();
        List<ItemStack> keep = event.getKeep();
        drops = event.getAllDrops();
        if (!keep.isEmpty()) {
            Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> {
                inv.clear();
                keep.forEach(inv::addItem);
                p.updateInventory();
                if (!p.isOnline()) {
                    p.saveData();
                }
            });
        }
        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                loc.getWorld().dropItemNaturally(loc, drop);
            }
        }
    }

    @EventHandler
    public void onJobsPayment(JobsPrePaymentEvent e) {
        User user = UserManager.get(e.getPlayer().getUniqueId());
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getEntity().hasPermission("elowyrcore.jobs.money.double")) {
            e.setAmount(e.getAmount() * 2);
        }
        if (user != null) {
            Boost moneyBoost = user.getBestBoost(BoostType.MONEY);
            Boost pointsBoost = user.getBestBoost(BoostType.POINTS);
            if (moneyBoost != null) {
                e.setAmount(e.getAmount() * moneyBoost.getValue());
            }
            if (pointsBoost != null) {
                e.setPoints(e.getPoints() * pointsBoost.getValue());
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        for (short durability : shorts) {
            if (item.getDurability() == durability) {
                InventoryUtils.decrementItem(event.getPlayer(), item, 1);
            }
        }
    }

    @EventHandler
    public void onJobsExpGain(JobsExpGainEvent e) {
        Boost xpBoost;
        User user = UserManager.get(e.getPlayer().getUniqueId());
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getEntity().hasPermission("elowyrcore.jobs.exp.double")) {
            e.setExp(e.getExp() * 2);
        }
        if (user != null && (xpBoost = user.getBestBoost(BoostType.EXPERIENCE)) != null) {
            e.setExp(e.getExp() * xpBoost.getValue());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if (!from.getWorld().equals(to.getWorld()) || to.distance(from) <= 0.1) {
            return;
        }
        BukkitTask task = Manager.INVEST_TELEPORTATION.remove(p.getUniqueId());
        if (task != null) {
            task.cancel();
            Lang.send(p, "invest.failed");
        }
    }

    private TextComponent componentFromItem(ItemStack item, String message) {
        String json = ItemBuilder.convertItemToJson(item);
        BaseComponent[] hoverEventComponents = new BaseComponent[]{new TextComponent(json)};
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);
        TextComponent component = new TextComponent(message);
        component.setHoverEvent(hoverEvent);
        return component;
    }
}
