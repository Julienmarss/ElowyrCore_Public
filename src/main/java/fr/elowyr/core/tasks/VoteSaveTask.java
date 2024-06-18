package fr.elowyr.core.tasks;

import fr.elowyr.core.voteparty.VoteManager;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteSaveTask extends BukkitRunnable {
    public void run() {
        VoteManager.get().save();
    }
}
