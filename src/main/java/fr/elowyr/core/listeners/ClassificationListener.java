package fr.elowyr.core.listeners;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.classement.data.UserKey;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.user.UserManager;
import org.bukkit.event.*;
import com.massivecraft.factions.event.*;
import org.bukkit.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;

public class ClassificationListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFactionDisband(final FactionDisbandEvent e) {
        GlobalClassificationManager.get().removeFaction(e.getFaction().getId());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFactionRename(final FactionRenameEvent e) {
        GlobalClassificationManager.get().setFactionTag(e.getFaction().getId(), e.getFactionTag());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJobsPayment(final JobsPaymentEvent e) {
        final double pts = e.get(CurrencyType.POINTS);
        final OfflinePlayer p = e.getPlayer();
        if (pts > 0.0) {
            GlobalClassificationManager.get().addUserValue(p.getUniqueId(), p.getName(), UserKey.JOBS_POINTS, pts);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player p = e.getEntity();
        final Player killer = p.getKiller();
        final GlobalClassificationManager gcm = GlobalClassificationManager.get();
        gcm.addUserValue(p.getUniqueId(), p.getName(), UserKey.DEATH, 1.0);
        if (killer != null) {
            gcm.addUserValue(killer.getUniqueId(), killer.getName(), UserKey.KILL, 1.0);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        final User user = UserManager.get(p.getUniqueId());
        if (user == null) {
            return;
        }
        final long elapsed = System.currentTimeMillis() - user.getLastClassificationUpdate();
        GlobalClassificationManager.get().addUserValue(p.getUniqueId(), p.getName(), UserKey.TIME_PLAYED, (int)(elapsed / 1000L));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent e) {
        final LivingEntity entity = e.getEntity();
        final Player killer = entity.getKiller();
        if (killer != null) {
            GlobalClassificationManager.get().addUserValue(killer.getUniqueId(), killer.getName(), UserKey.MOB_KILL, 1.0);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        GlobalClassificationManager.get().addUserValue(p.getUniqueId(), p.getName(), UserKey.BLOCK_BROKEN, 1.0);
    }
}
