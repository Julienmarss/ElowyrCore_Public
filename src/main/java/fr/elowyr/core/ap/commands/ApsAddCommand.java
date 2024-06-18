package fr.elowyr.core.ap.commands;

import fr.elowyr.core.ap.APManager;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.Player;

public class ApsAddCommand extends TCommand {

    private final APManager manager = APManager.get();

    @Override
    @Command(name = {"ap.add", "ap.create", "aps.add", "aps.create"}, permissionNode = "Elowyrcore.ap.add")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();
        if(args.length() < 2) {
            Lang.send(player, "ap.add.usage");
            return;
        }
        if(!Utils.isInt(args.getArgs(0))) {
            Lang.send(player, "ap.add.usage");
            return;
        }
        final int price = Integer.parseInt(args.getArgs(0));
        final boolean vip = false;

        this.manager.addAp(player, price, vip);
        Lang.send(player, "ap.add.success", "price", price, "vip", vip);
    }
}
