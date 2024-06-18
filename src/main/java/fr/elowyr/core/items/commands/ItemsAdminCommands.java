package fr.elowyr.core.items.commands;

import fr.elowyr.core.items.guis.ItemsGUI;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ItemsAdminCommands extends TCommand {


    @Override
    @Command(name = "items.admin", permissionNode = "elowyrcore.items")
    public void onCommand(CommandArgs args) {
        ItemsGUI gui = new ItemsGUI(1, args.getPlayer(), ItemsManager.getInstance());
        gui.open(args.getPlayer());
    }
}
