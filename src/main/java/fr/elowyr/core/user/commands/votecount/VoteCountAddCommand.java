package fr.elowyr.core.user.commands.votecount;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class VoteCountAddCommand extends TCommand {

    @Override
    @Command(name = "votecount.add", permissionNode = "elowyrcore.votecount.add")
    public void onCommand(CommandArgs args) {
        if (args.length() != 2) {
            Lang.send(args.getSender(), "votecount.add.usage");
            return;
        }
        String name = args.getArgs(0);
        Integer value = parseInt(args.getSender(), args.getArgs(1));
        if (value == null)
            return;
        UserManager.getOrLoadByUsername(name, user -> {
            if (user == null) {
                Lang.send(args.getSender(), "votecount.add.no-user", "name", name);
                return;
            }
            user.setVotes(user.getVotes() + value);
            DbUtils.updateUser(user, "votes", user.getVotes());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "classification give-player votecount " + name + " " + value);
            Lang.send(args.getSender(), "votecount.add.success", "name", name, "value", value);
        });
    }

    private Integer parseInt(final CommandSender sender, final String value) {
        try {
            final int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                Lang.send(sender, "votecount.add.negative-number");
                return null;
            }
            return parsed;
        }
        catch (Throwable ex) {
            Lang.send(sender, "votecount.add.invalid-number");
            return null;
        }
    }
}
