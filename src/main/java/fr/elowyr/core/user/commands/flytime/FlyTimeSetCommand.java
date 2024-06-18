package fr.elowyr.core.user.commands.flytime;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class FlyTimeSetCommand extends TCommand {

    @Override
    @Command(name = "flytime.set", permissionNode = "elowyrcore.flytime.set")
    public void onCommand(CommandArgs args) {
        if (args.length() < 2) {
            Lang.send(args.getSender(), "flytime.set.usage");
            return;
        }
        final String name = args.getArgs(0);
        int amount;
        try {
            amount = Integer.parseInt(args.getArgs(1));
        }
        catch (Throwable ex) {
            Lang.send(args.getSender(), "flytime.set.invalid-amount");
            return;
        }
        if (amount <= 0) {
            Lang.send(args.getSender(), "flytime.set.negative-amount");
            return;
        }
        UserManager.getOrLoadByUsername(name, user -> {
            if (user == null) {
                Lang.send(args.getSender(), "flytime.set.no-user", "name", name);
            }
            else {
                user.setFlyTime(amount);
                DbUtils.updateUser(user, "fly_time", user.getFlyTime());
                Lang.send(args.getSender(), "flytime.set.success", "name", user.getUsername(), "time", amount);
            }
        });
    }
}
