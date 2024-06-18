package fr.elowyr.core.tasks;

import fr.elowyr.core.classement.GlobalClassificationManager;
import org.bukkit.scheduler.*;

public class ClassificationSaveTask extends BukkitRunnable {

    private static final byte SAVE_TYPE = 3;
    
    public void run() {
        final long start = System.currentTimeMillis();
        final int changes = GlobalClassificationManager.get().saveSync((byte)3);
        final long elapsed = System.currentTimeMillis() - start;
        System.out.println("Classification saved in " + elapsed + " ms (" + changes + " changes)");
    }
}
