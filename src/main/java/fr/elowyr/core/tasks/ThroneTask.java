package fr.elowyr.core.tasks;

import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.throne.ThroneManager;
import fr.elowyr.core.utils.VaultUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ThroneTask extends BukkitRunnable {

    public void run() {
        final List<Player> players = ThroneManager.get().getPlayersInZone();
        if (players.size() == 1) {
            final Player player = players.get(0);
            double money;
            if (player.hasPermission("elowyrcore.throne.double")) {
                money = ThroneManager.get().getRewardMoney() * 2;
            } else {
                money = ThroneManager.get().getRewardMoney();
            }
            VaultUtils.depositMoney(player, money);
            Lang.send(player, "listeners.throne.win-money", "money", String.format(Config.get().getFloatFormat(), money));
        }
        else if (!players.isEmpty()) {
            for (final Player player : players) {
                Lang.send(player, "listeners.throne.too-many");
            }
        }
    }
}
