package fr.elowyr.core.tasks;

import fr.elowyr.core.ElowyrCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class ResetVoteCountTask extends BukkitRunnable {

    private long delay;

    public ResetVoteCountTask() {
        this.delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public void run() {
        if (!this.hasExpired()) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "classification reset votecount");
        System.out.println("Réinitialisation des VoteCounts !");
        Bukkit.getScheduler().cancelTask(ElowyrCore.getInstance().getTaskID());
        resetDelay();
    }

    private long getRemaining() {
        return this.delay - System.currentTimeMillis();
    }

    public void resetDelay() {
        this.delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
    }

    public boolean hasExpired() {
        return this.getRemaining() <= 0L;
    }
}
