package fr.elowyr.core.listeners.events;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.outpost.OutpostsManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OutPostListener implements Listener {
    public static final OutPostListener INSTANCE = new OutPostListener();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom(), to = e.getTo();
        if (from.distance(to) <= 0.01D)
            return;
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(p);
        if (!fplayer.getFaction().isNormal())
            return;
        for (OutPost outPost : OutpostsManager.get().getActiveOutPosts()) {
            boolean inArea = outPost.getArea().isInArea(to);
            if (inArea) {
                outPost.enter(fplayer);
                continue;
            }
            outPost.exit(fplayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        FPlayer fp = FPlayers.getInstance().getByPlayer(e.getPlayer());
        Location to = e.getTo();
        for (OutPost outPost : OutpostsManager.get().getActiveOutPosts()) {
            boolean inArea = outPost.getArea().isInArea(to);
            if (inArea) {
                outPost.enter(fp);
                continue;
            }
            outPost.exit(fp);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        FPlayer fp = FPlayers.getInstance().getByPlayer(p);
        for (OutPost outPost : OutpostsManager.get().getActiveOutPosts())
            outPost.exit(fp);
    }

    @EventHandler
    public void onFactionMemberChanged(FPlayerLeaveEvent e) {
        FPlayer fp = e.getfPlayer();
        for (OutPost outPost : OutpostsManager.get().getActiveOutPosts())
            outPost.exit(fp);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFactionDisband(FactionDisbandEvent e) {
        disbandFaction(e.getFaction());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        FPlayer fp = FPlayers.getInstance().getByPlayer(e.getEntity());
        if (!fp.getFaction().isNormal())
            return;
        for (OutPost outPost : OutpostsManager.get().getActiveOutPosts())
            outPost.exit(fp);
    }

    private void disbandFaction(Faction faction) {
        for (FPlayer fp : faction.getFPlayersWhereOnline(true)) {
            for (OutPost outPost : OutpostsManager.get().getActiveOutPosts())
                outPost.exit(fp);
        }
    }
}