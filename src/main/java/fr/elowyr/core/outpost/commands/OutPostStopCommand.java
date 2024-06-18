package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class OutPostStopCommand extends TCommand {

    @Command(name = "outpost.stop", permissionNode = "elowyrcore.event.outpost.stop")
    public void onCommand(CommandArgs args) {
        OutpostsManager.get().stop(false);
        Lang.send(args.getSender(), "event.outpost.stop.success");
    }
}
