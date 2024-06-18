package fr.elowyr.core.tasks;

import fr.elowyr.core.user.data.User;
import fr.elowyr.core.classement.data.UserKey;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.user.UserManager;
import org.bukkit.scheduler.*;

public class ClassificationTimePlayedTask extends BukkitRunnable {

    public void run() {
        final long now = System.currentTimeMillis();
        final GlobalClassificationManager gcm = GlobalClassificationManager.get();
        for (final User user : UserManager.getOnlineUsers()) {
            final long elapsed = now - user.getLastClassificationUpdate();
            user.setLastClassificationUpdate(now);
            gcm.addUserValueNoUpdate(user.getUniqueId(), user.getUsername(), UserKey.TIME_PLAYED, (int)(elapsed / 1000L));
        }
        gcm.updateUsers(UserKey.TIME_PLAYED);
    }

}
