package fr.elowyr.core.listeners;

import fr.elowyr.core.throne.ThroneManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class ThroneListener implements Listener
{
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final ThroneManager throneManager = ThroneManager.get();
        final boolean fromIn = throneManager.getZone().isInArea(from);
        final boolean toIn = throneManager.getZone().isInArea(to);
        final List<Player> players = throneManager.getPlayersInZone();
        if (fromIn && !toIn) {
            players.remove(player);
            throneManager.checkPlayers();
        }
        else if (!fromIn && toIn && !players.contains(player)) {
            players.add(player);
            throneManager.checkPlayers();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player p = event.getPlayer();
        final ThroneManager throneManager = ThroneManager.get();
        throneManager.getPlayersInZone().remove(p);
        throneManager.checkPlayers();
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final ThroneManager throneManager = ThroneManager.get();
        throneManager.getPlayersInZone().remove(event.getPlayer());
        throneManager.checkPlayers();
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final ThroneManager throneManager = ThroneManager.get();
        throneManager.getPlayersInZone().remove(event.getEntity());
        throneManager.checkPlayers();
    }
}
