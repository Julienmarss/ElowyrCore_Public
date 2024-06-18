package fr.elowyr.core.commands.events;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.IEvent;
import fr.elowyr.core.enums.StopReason;
import fr.elowyr.core.managers.EventsManager;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

public class EventStopCommand extends TCommand {

    private final EventsManager eventsManager = EventsManager.getInstance();

    @Command(name = "event.stop", permissionNode = "elowyrcore.admin")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();

        if(args.length() == 0) {
            player.sendMessage(Utils.color("&c/event stop (nom)"));
            return;
        }

        Optional<IEvent> optionalTEvent = this.eventsManager.getEvent(args.getArgs(0));

        if(!optionalTEvent.isPresent()) {
            player.sendMessage(Utils.color("&cCet event n'existe pas"));
            return;
        }

        IEvent event = optionalTEvent.get();

        if(!event.isStarted()) {
            player.sendMessage(Utils.color("&cCet event n'est pas lancé !"));
            return;
        }
        event.stopEvent(StopReason.PLAYER);

        player.sendMessage(Utils.color("&fVous venez d'arreter l'event &e" + event.getName() + " !"));
    }
}
