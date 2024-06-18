package fr.elowyr.core.listeners;

import fr.elowyr.core.lang.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class StartingListener implements Listener
{
    public static final StartingListener INSTANCE = new StartingListener();
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        e.setKickMessage(Lang.get().getString("listeners.starting"));
    }
}
