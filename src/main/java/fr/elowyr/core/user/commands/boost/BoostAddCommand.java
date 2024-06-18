package fr.elowyr.core.user.commands.boost;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.Boost;
import fr.elowyr.core.data.BoostType;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.TimeUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.command.*;

public class BoostAddCommand extends TCommand {

    @Override
    @Command(name = "boost.add", permissionNode = "elowyrcore.boost.add", isConsole = true)
    public void onCommand(CommandArgs args) {
        if (args.length() < 4) {
            Lang.send(args.getSender(), "boost.add.usage");
            return;
        }
        String name = args.getArgs(0);
        BoostType type = BoostType.getByType(args.getArgs(1));
        if (type == null) {
            Lang.send(args.getSender(), "boost.add.invalid-type", "type", args.getArgs(1));
            return;
        }
        Integer value = parseInt(args.getSender(), args.getArgs(2), "value");
        if (value == null)
            return;
        Integer time = parseInt(args.getSender(), args.getArgs(3), "time");
        if (time == null)
            return;
        UserManager.getOrLoadByUsername(name, user -> {
            if (user == null) {
                Lang.send(args.getSender(), "boost.add.no-user", "name", name);
                return;
            }
            Boost boost = user.getBoost(type, value);
            if (boost == null) {
                boost = new Boost(user, type, value, time);
                DbUtils.insertBoost(boost);
                user.addBoost(boost);
            } else {
                boost.addTime(time);
                DbUtils.updateBoost(boost, "time", boost.getTime());
            }
            Lang.send(args.getSender(), "boost.add.success", "name", name, "time", TimeUtils.format(time), "value", value, "type", type.getLang());
            Lang.send(user.getPlayer(), "boost.add.success-other", "time", TimeUtils.format(time), "value", value, "type", type.getLang());
        });
    }
    
    private Integer parseInt(final CommandSender sender, final String value, final String key) {
        try {
            final int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                Lang.send(sender, "boost.add.negative-number." + key);
                return null;
            }
            return parsed;
        }
        catch (Throwable ex) {
            Lang.send(sender, "boost.add.invalid-number." + key);
            return null;
        }
    }
}
