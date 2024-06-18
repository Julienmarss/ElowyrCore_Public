package fr.elowyr.core.commands;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.tasks.InvestTeleportationTask;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class InvestCommand extends TCommand {

    @Command(name = "invest", permissionNode = "elowyrcore.invest")
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        if (Manager.INVEST_TELEPORTATION.containsKey(player.getUniqueId())) {
            Lang.send(player, "invest.already-running");
            return;
        }
        BukkitTask task = new InvestTeleportationTask(player).runTaskTimer(ElowyrCore.getInstance(), 0L, 20L);
        Manager.INVEST_TELEPORTATION.put(player.getUniqueId(), task);
    }
}
