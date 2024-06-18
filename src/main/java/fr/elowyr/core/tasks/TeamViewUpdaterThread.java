package fr.elowyr.core.tasks;

import com.google.common.collect.Maps;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Factions;

import java.util.Map;
import java.util.UUID;

public class TeamViewUpdaterThread extends Thread {

    public TeamViewUpdaterThread() {
        this.setName("Team View Updater Thread");
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.tick();
                Thread.sleep(100L); // Sleep 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        Factions.getInstance().getAllFactions().forEach(faction -> {
            FPlayer admin = faction.getFPlayerAdmin();

            if (admin == null) {
                return;
            }

            UUID leader = admin.getPlayer().getUniqueId();
            Map<UUID, Map<String, Double>> positions = Maps.newHashMap();

            faction.getOnlinePlayers().forEach(member -> {
                UUID uuid = member.getUniqueId();
                Map<String, Double> position = positions.computeIfAbsent(uuid, id -> Maps.newHashMap());

                if (member.getWorld().getName().equalsIgnoreCase("world")) {
                    position.put("x", member.getLocation().getX());
                    position.put("y", member.getLocation().getY() + 4);
                    position.put("z", member.getLocation().getZ());
                } else {
                    position.put("x", 0.0);
                    position.put("y", 0.0);
                    position.put("z", 0.0);
                }
            });

            LCPacketTeammates teammates = new LCPacketTeammates(leader, System.currentTimeMillis(), positions);

            faction.getOnlinePlayers().forEach(member -> {
                LunarClientAPI.getInstance().sendTeammates(member, teammates);
            });
        });
    }
}
