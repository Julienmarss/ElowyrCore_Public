package fr.elowyr.core.items.commands;

import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ItemsCommand extends TCommand {
    
    @Override
    @Command(name = "items", permissionNode = "elowyrcore.items")
    public void onCommand(CommandArgs args) {
        Lang.send(args.getSender(), "items.usage");
    }
}
