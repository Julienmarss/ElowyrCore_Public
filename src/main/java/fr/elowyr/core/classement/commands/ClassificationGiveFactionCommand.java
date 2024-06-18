package fr.elowyr.core.classement.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.classement.data.FactionKey;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import com.massivecraft.factions.*;

public class ClassificationGiveFactionCommand extends TCommand {
    
    @Override
    @Command(name = "classification.give-faction", permissionNode = "elowyrcore.classification.give.faction", isConsole = true)
    public void onCommand(CommandArgs args) {
        if (args.length() < 3) {
            Lang.send(args.getSender(), "classification.give-faction.usage");
            return;
        }
        final FactionKey key = FactionKey.fromName(args.getArgs(0));
        final String factionTag = args.getArgs(1);
        if (key == null) {
            Lang.send(args.getSender(), "classification.give-faction.unknown-type", "type", args.getArgs(0));
            return;
        }
        final Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction == null) {
            Lang.send(args.getSender(), "classification.give-faction.no-faction", "name", factionTag);
            return;
        }
        if (!faction.isNormal()) {
            Lang.send(args.getSender(), "classification.give-faction.invalid-faction", "name", faction.getTag());
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args.getArgs(2));
        }
        catch (Throwable ex) {
            Lang.send(args.getSender(), "classification.give-faction.invalid-amount");
            return;
        }
        if (amount <= 0) {
            Lang.send(args.getSender(), "classification.give-faction.negative-amount");
            return;
        }
        GlobalClassificationManager.get().addFactionValue(faction, key, amount);
        Lang.send(args.getSender(), "classification.give-faction.success", "name", factionTag, "key", key.getName(), "amount", amount);
    }
}
