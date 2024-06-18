package fr.elowyr.core.items.commands;

import fr.elowyr.core.items.data.Item;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;

import java.util.stream.Collectors;

public class ItemsHelpCommand extends TCommand {

    @Override
    @Command(name = "items.help", permissionNode = "elowyrcore.items")
    public void onCommand(CommandArgs args) {
        Lang.send(args.getSender(), "items.help", "items", ItemsManager.getInstance().getALL().stream().map(Item::getName).collect(Collectors.joining(", ")));
    }
}
