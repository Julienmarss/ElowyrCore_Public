package fr.elowyr.core.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.lunar.LunarManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LunarListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(ElowyrCore.getInstance(), () -> {
            for (LCWaypoint waypoint : LunarManager.get().getWaypoints())
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            if (player.hasPermission("staff.use") && LunarClientAPI.getInstance().isRunningLunarClient(player)) {
                LunarClientAPI.getInstance().giveAllStaffModules(player);
                player.sendMessage("§fVous avez reçu les §amodules modérateur §f(§bLunar-Client§f) §f-> §7§oAppuie sur maj pour ouvrir le GUI.");
            }
        }, 60L);
    }
}
