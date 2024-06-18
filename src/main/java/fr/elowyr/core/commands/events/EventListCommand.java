package fr.elowyr.core.commands.events;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.managers.EventsManager;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class EventListCommand extends TCommand {

    private final EventsManager eventsManager = EventsManager.getInstance();

    @Command(name = "event.list", permissionNode = "elowyrcore.admin")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();

        String events = this.eventsManager.getTEvents().keySet()
                .stream()
                .collect(Collectors.joining("&f, &c"));

        player.sendMessage(Utils.color("&fListe des évents disponibles: &c" + events));
    }
}
