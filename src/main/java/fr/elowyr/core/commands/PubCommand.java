package fr.elowyr.core.commands;

import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;

public class PubCommand extends TCommand {

    @Command(name = "pub", permissionNode = "elowyrcore.pub")
    public void onCommand(CommandArgs args) {
        if (args.length() == 0) {
            Lang.send(args.getSender(), "pub.usage");
            return;
        }
        Lang.broadcast("pub.message", "sender", args.getSender().getName(), "message", Utils.color(String.join(" ", args.getArgs())));
    }
}
