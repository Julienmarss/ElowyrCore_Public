package fr.elowyr.core.listeners.events;

import fr.elowyr.core.events.EventStartEvent;
import fr.elowyr.core.events.EventStopEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventsListener implements Listener {

    @EventHandler
    public void onEventStart(EventStartEvent event) {
        //ScoreboardManager.getInstance().setCurrentBoard();
        //ScoreboardManager.get().useScoreboard(event.getEvent().getName().toLowerCase(Locale.ROOT));
    }

    @EventHandler
    public void onEventStop(EventStopEvent event) {
        //ScoreboardManager.get().useScoreboard("global");
    }
}
