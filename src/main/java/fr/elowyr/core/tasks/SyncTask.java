package fr.elowyr.core.tasks;

import fr.elowyr.core.ElowyrCore;
import org.bukkit.Bukkit;

public class SyncTask implements Runnable
{
    private final Runnable task;
    
    public SyncTask(final Runnable task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), this.task);
    }
}
