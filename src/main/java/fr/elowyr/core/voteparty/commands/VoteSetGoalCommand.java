package fr.elowyr.core.voteparty.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.voteparty.VoteManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class VoteSetGoalCommand extends TCommand {

    @Override
    @Command(name = {"vp.setgoal","voteparty.setgoal"}, permissionNode = "elowyrcore.voteparty.setgoal")
    public void onCommand(CommandArgs args) {
        if (args.length() < 1) {
            Lang.send(args.getSender(), "voteparty.setgoal.usage");
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args.getArgs(0));
        } catch (Throwable ex) {
            Lang.send(args.getSender(), "voteparty.setgoal.invalid-amount");
            return;
        }
        if (amount < 0) {
            Lang.send(args.getSender(), "voteparty.setgoal.negative-amount");
            return;
        }
        VoteManager.get().setServerGoal(amount);
        Lang.send(args.getSender(), "voteparty.setgoal.success", "amount", amount);
    }
}
