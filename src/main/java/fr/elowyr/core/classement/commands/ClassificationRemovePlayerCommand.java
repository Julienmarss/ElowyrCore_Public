package fr.elowyr.core.classement.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.classement.data.FactionKey;
import fr.elowyr.core.classement.data.FieldKey;
import fr.elowyr.core.classement.data.UserKey;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.*;
import org.bukkit.entity.*;
import com.massivecraft.factions.*;

public class ClassificationRemovePlayerCommand extends TCommand {

    @Override
    @Command(name = "classification.remove-player", permissionNode = "elowyrcore.classification.remove.player")
    public void onCommand(CommandArgs args) {
        if (args.length() < 3) {
            Lang.send(args.getSender(), "classification.remove-player.usage");
            return;
        }
        final String type = args.getArgs(0);
        FieldKey key = UserKey.fromName(type);
        final String name = args.getArgs(1);
        final Player player = Bukkit.getPlayer(name);
        if (player == null || !player.isOnline()) {
            Lang.send(args.getSender(), "classification.remove-player.no-faction", "name", name);
            return;
        }
        if (key == null) {
            key = FactionKey.fromName(type);
            if (key == null) {
                Lang.send(args.getSender(), "classification.remove-player.unknown-type", "type", args.getArgs(0));
                return;
            }
            final FPlayer fp = FPlayers.getInstance().getByPlayer(player);
            Bukkit.dispatchCommand(args.getSender(), "classification remove-faction " + type + " " + fp.getFaction().getTag() + " " + args.getArgs(2));
        } else {
            int amount;
            try {
                amount = Integer.parseInt(args.getArgs(2));
            } catch (Throwable ex) {
                Lang.send(args.getSender(), "classification.remove-player.invalid-amount");
                return;
            }
            if (amount <= 0) {
                Lang.send(args.getSender(), "classification.remove-player.negative-amount");
                return;
            }
            GlobalClassificationManager.get().addUserValue(player.getUniqueId(), player.getName(), (UserKey)key, -amount);
            Lang.send(args.getSender(), "classification.remove-player.success", "name", name, "key", key.getName(), "amount", amount);
        }
    }
}
