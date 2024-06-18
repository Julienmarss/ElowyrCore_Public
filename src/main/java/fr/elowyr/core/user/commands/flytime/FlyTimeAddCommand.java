package fr.elowyr.core.user.commands.flytime;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class FlyTimeAddCommand extends TCommand {

    @Override
    @Command(name = "flytime.add", permissionNode = "elowyrcore.flytime.add", isConsole = true)
    public void onCommand(CommandArgs args) {
        if (args.length() < 2) {
            Lang.send(args.getSender(), "flytime.add.usage");
            return;
        }
        final String name = args.getArgs(0);
        int amount;
        try {
            amount = Integer.parseInt(args.getArgs(1));
        }
        catch (Throwable ex) {
            Lang.send(args.getSender(), "flytime.add.invalid-amount");
            return;
        }
        if (amount <= 0) {
            Lang.send(args.getSender(), "flytime.add.negative-amount");
            return;
        }
        UserManager.getOrLoadByUsername(name, user -> {
            if (user == null) {
                Lang.send(args.getSender(), "flytime.add.no-user", "name", name);
            }
            else {
                user.setFlyTime(user.getFlyTime() + amount);
                DbUtils.updateUser(user, "fly_time", user.getFlyTime());
                Lang.send(args.getSender(), "flytime.add.success", "name", user.getUsername(), "time", amount);
            }
        });
    }
}
