package fr.elowyr.core.listeners.events;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import fr.elowyr.core.data.events.Largage;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class LargageListener implements Listener {

    private final Largage largage;

    public LargageListener(Largage largage) {
        this.largage = largage;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if(!this.largage.isFinished()) {
            this.largage.getChestWaypoints().forEach((block, lcWaypoint) -> LunarClientAPI.getInstance().sendWaypoint(player, lcWaypoint));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(block != null) {
            if(block.getLocation() != null) {
                this.largage.getChestLocations().forEach(location -> {
                    if(location != null) {
                        if(this.compareLocation(block.getLocation(), location)) {
                            if(block.hasMetadata("LARGAGE")) {
                                event.setCancelled(true);
                                int random = Utils.randomInt(0, this.largage.getLoots().size());
                                String command = this.largage.getLoots().get(random);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                            }
                        }
                    }
                });
                LCWaypoint waypoint = this.largage.getChestWaypoints().get(block);
                Bukkit.getOnlinePlayers().forEach(player1 -> LunarClientAPI.getInstance().removeWaypoint(player1, waypoint));
                this.largage.getChestWaypoints().remove(block);
                block.removeMetadata("LARGAGE", this.largage.getPlugin());
                block.setType(Material.AIR);
                this.largage.getChestLocations().remove(block.getLocation());
                Lang.broadcast("events.largage.chest-found", "player", player.getName());
            }
        }
    }

    public boolean compareLocation(Location first, Location second) {
        if(first.getWorld() == second.getWorld()
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ()) {
            return true;
        }
        return false;
    }
}
