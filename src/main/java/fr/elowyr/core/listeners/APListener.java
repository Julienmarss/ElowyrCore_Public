package fr.elowyr.core.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import fr.elowyr.core.ap.data.AP;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.ap.APManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;
import java.util.Optional;

public class APListener implements Listener {

    private final APManager aps = APManager.get();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (event.isCancelled()) return;

        Chunk chunk = event.getBlock().getChunk();
        String chunkAsString = this.aps.changeChunkToString(chunk);
        Optional<AP> optional = Optional.ofNullable(this.aps.getAps().get(chunkAsString));

        if (!optional.isPresent()) return;

        AP ap = optional.get();

        ap.removeBlocks(event.getBlock().getType());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (event.isCancelled())
            return;

        Chunk chunk = event.getBlock().getChunk();
        String chunkAsString = this.aps.changeChunkToString(chunk);
        Optional<AP> optional = Optional.ofNullable(this.aps.getAps().get(chunkAsString));

        if (!optional.isPresent())
            return;

        AP ap = optional.get();
        event.setCancelled(ap.addBlocks(event.getBlock().getType(), player));
    }

    @EventHandler
    public void onDisband(FactionDisbandEvent event) {
        this.aps.removeAllClaims(event.getFaction().getId());
    }

    @EventHandler
    public void onUnclaimAll(LandUnclaimAllEvent event) {
        this.aps.removeAllClaims(event.getFaction().getId());
    }

    @EventHandler
    public void onUnclaim(LandUnclaimEvent event) {
        this.aps.removeClaims(event.getLocation());
    }

    @EventHandler
    public void onClaim(LandClaimEvent event) {
        final FLocation location = event.getLocation();
        final Faction faction = event.getFaction();
        Faction oldFaction = Board.getInstance().getFactionAt(location);
        if (!oldFaction.isWilderness()) {
            for (Map.Entry<String, AP> entry : aps.getAps().entrySet()) {
                if (entry.getValue().isLocation(location)) {
                    entry.getValue().removeClaims();
                    entry.getValue().setCurrentOwnerId(faction.getId());
                }
            }
        } else if (oldFaction.isWilderness()) {
            if (aps.getAps().containsKey(this.aps.changeChunkToString(location.getChunk()))) {
                event.setCancelled(true);
                Lang.send(event.getfPlayer().getPlayer(), "ap.claim.error");
            }
        }
    }
}