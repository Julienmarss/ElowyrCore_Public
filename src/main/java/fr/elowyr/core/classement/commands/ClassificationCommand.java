package fr.elowyr.core.classement.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ClassificationCommand extends TCommand {

    @Override
    @Command(name = "classification", permissionNode = "elowyrcore.classification")
    public void onCommand(CommandArgs args) {
        Lang.send(args.getSender(), "classification.usage");
    }
}
