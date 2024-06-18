package fr.elowyr.core.voteparty.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.voteparty.VoteManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class VoteCommand extends TCommand {

    @Override
    @Command(name = {"vp","voteparty"}, permissionNode = "elowyrcore.voteparty")
    public void onCommand(CommandArgs args) {
        if (args.length() == 0) {
            if (args.getPlayer().hasPermission("elowyrcore.voteparty.show-usage")) {
                Lang.send(args.getSender(), "voteparty.usage");
            }
            Lang.send(args.getSender(), "voteparty.global", "current", VoteManager.get().getServerCurrent(), "goal", VoteManager.get().getServerGoal());
        }
    }
}
