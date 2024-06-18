package fr.elowyr.core.user.commands.boost;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.Boost;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.TimeUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.*;

import java.util.List;
import java.util.stream.*;

public class BoostCommand extends TCommand {

    @Override
    @Command(name = "boost", permissionNode = "elowyrcore.boost")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();
        final User user = UserManager.get(player.getUniqueId());
        final List<Boost> boosts = user.getBoosts().collect(Collectors.toList());
        if (boosts.isEmpty()) {
            Lang.send(player, "boost.boosts.empty");
        }
        else {
            Lang.send(player, "boost.boosts.header", "size", boosts.size());
            for (final Boost boost : boosts) {
                Lang.send(player, "boost.boosts.entry", "type", boost.getType().getLang(), "value", boost.getValue(), "time", TimeUtils.format(boost.getTime()));
            }
        }
    }
}
