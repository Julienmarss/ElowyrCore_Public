package fr.elowyr.core.tasks;

import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.managers.Manager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InvestTeleportationTask extends BukkitRunnable {
    private final Player player;
    private long time;

    public InvestTeleportationTask(Player player) {
        this.player = player;
        this.time = Config.get().getInvestTeleportationTime();
    }

    public void run() {
        if (this.time == 0L) {
            Manager.INVEST_TELEPORTATION.remove(this.player.getUniqueId());
            Lang.send(this.player, "invest.teleportation-success");
            Manager.sendToServer(this.player, Config.get().getInvestServerName());
            this.cancel();
        }
        if (Config.get().getInvestTeleportationMessageTimes().contains(this.time)) {
            Lang.send(this.player, "invest.teleportation-times." + this.time);
        }
        --this.time;
    }
}
