package fr.elowyr.core.listeners;

import com.massivecraft.factions.event.FactionDisbandEvent;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.outpost.OutpostsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionOutPostListener implements Listener {
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent e) {
        for (OutPost outPost : OutpostsManager.get().getOutPosts(e.getFaction().getId()))
            outPost.free();
        OutpostsManager.get().save();
    }
}
