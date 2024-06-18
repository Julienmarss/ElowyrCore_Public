package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class OutpostCommand extends TCommand {

    @Command(name = "outpost", permissionNode = "elowyrcore.event.outpost")
    public void onCommand(CommandArgs args) {
        Lang.send(args.getSender(), "event.outpost.usage");
    }
}
