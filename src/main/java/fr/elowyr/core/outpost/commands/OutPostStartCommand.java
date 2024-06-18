package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class OutPostStartCommand extends TCommand {

    @Command(name = "outpost.start", permissionNode = "elowyrcore.event.outpost.start")
    public void onCommand(CommandArgs args) {
        OutpostsManager.get().start();
        Lang.send(args.getSender(), "event.outpost.start.success");
    }
}
