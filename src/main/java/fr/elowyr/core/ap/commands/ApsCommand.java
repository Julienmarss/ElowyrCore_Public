package fr.elowyr.core.ap.commands;


import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.ap.APManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ApsCommand extends TCommand {

    @Override
    @Command(name = {"ap", "aps"}, permissionNode = "elowyrcore.ap")
    public void onCommand(CommandArgs args) {
        APManager.get().buyApCheck(args.getPlayer());
    }
}
