package fr.elowyr.core.voteparty.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.voteparty.VoteManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class VoteAddCommand extends TCommand {

    @Override
    @Command(name = {"vp.addvote","voteparty.addvote"}, permissionNode = "elowyrcore.voteparty.add")
    public void onCommand(CommandArgs args) {
        if (args.length() < 1) {
            Lang.send(args.getSender(), "voteparty.addvote.usage");
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args.getArgs(0));
        } catch (Throwable ex) {
            Lang.send(args.getSender(), "voteparty.addvote.invalid-amount");
            return;
        }
        if (amount < 0) {
            Lang.send(args.getSender(), "voteparty.addvote.negative-amount");
            return;
        }
        VoteManager.get().setServerCurrent(amount + VoteManager.get().getServerCurrent(), amount);
        Lang.send(args.getSender(), "voteparty.addvote.success", "amount", amount);
    }
}
