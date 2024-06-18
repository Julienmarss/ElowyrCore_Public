package fr.elowyr.core.ap.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ApsHelpCommand extends TCommand {

    @Override
    @Command(name = {"ap.help", "aps.help"}, permissionNode = "elowyrcore.ap.help")
    public void onCommand(CommandArgs args) {
        Lang.send(args.getSender(), args.getPlayer().isOp() ? "ap.usage-admin" : "ap.usage");
    }
}
