package fr.elowyr.core.user.commands.flytime;


import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class FlyTimeCommand extends TCommand {
    
    @Override
    @Command(name = "flytime", permissionNode = "elowyrcore.flytime")
    public void onCommand(CommandArgs args) {
        if (args.length() < 1) {
            Lang.send(args.getSender(), "flytime.usage");
            return;
        }
        final String name = args.getArgs(0);
        UserManager.getOrLoadByUsername(name, user -> {
            if (user == null) {
                Lang.send(args.getSender(), "flytime.show.no-user", "name", name);
            }
            else {
                Lang.send(args.getSender(), "flytime.show.success", "name", user.getUsername(), "time", user.getFlyTime());
            }
        });
    }
}
