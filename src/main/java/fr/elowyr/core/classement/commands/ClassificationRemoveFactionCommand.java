package fr.elowyr.core.classement.commands;

import com.massivecraft.factions.*;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.classement.data.FactionKey;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ClassificationRemoveFactionCommand extends TCommand {

    @Override
    @Command(name = "classification.remove-faction", permissionNode = "elowyrcore.classification.remove.faction")
    public void onCommand(CommandArgs args) {
        if (args.length() < 3) {
            Lang.send(args.getSender(), "classification.remove-faction.usage");
            return;
        }
        final FactionKey key = FactionKey.fromName(args.getArgs(0));
        final String factionTag = args.getArgs(1);
        if (key == null) {
            Lang.send(args.getSender(), "classification.remove-faction.unknown-type", "type", args.getArgs(0));
            return;
        }
        final Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction == null) {
            Lang.send(args.getSender(), "classification.remove-faction.no-faction", "name", factionTag);
            return;
        }
        if (!faction.isNormal()) {
            Lang.send(args.getSender(), "classification.remove-faction.invalid-faction", "name", faction.getTag());
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args.getArgs(2));
        }
        catch (Throwable ex) {
            Lang.send(args.getSender(), "classification.remove-faction.invalid-amount");
            return;
        }
        if (amount <= 0) {
            Lang.send(args.getSender(), "classification.remove-faction.negative-amount");
            return;
        }
        GlobalClassificationManager.get().addFactionValue(faction, key, -amount);
        Lang.send(args.getSender(), "classification.remove-faction.success", "name", factionTag, "key", key.getName(), "amount", amount);
    }
}
