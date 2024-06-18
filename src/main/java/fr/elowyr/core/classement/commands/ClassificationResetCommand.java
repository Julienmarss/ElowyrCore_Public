package fr.elowyr.core.classement.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.classement.data.FactionKey;
import fr.elowyr.core.classement.data.FieldKey;
import fr.elowyr.core.classement.data.UserKey;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ClassificationResetCommand extends TCommand {

    @Override
    @Command(name = "classification.reset", permissionNode = "elowyrcore.classification.reset")
    public void onCommand(CommandArgs args) {
        if (args.getArgs().length < 1) {
            Lang.send(args.getSender(), "classification.reset.usage");
            return;
        }
        final String type = args.getArgs(0);
        if (type.equalsIgnoreCase("all")) {
            GlobalClassificationManager.get().resetAll();
            Lang.send(args.getSender(), "classification.reset.success.all");
            return;
        }
        FieldKey key = UserKey.fromName(type);
        if (key != null) {
            GlobalClassificationManager.get().resetUsers(key);
            Lang.send(args.getSender(), "classification.reset.success.users", "type", type);
            return;
        }
        key = FactionKey.fromName(type);
        if (key != null) {
            GlobalClassificationManager.get().resetFactions(key);
            Lang.send(args.getSender(), "classification.reset.success.faction", "type", type);
            return;
        }
        Lang.send(args.getSender(), "classification.reset.unknown-type", "type", type);
    }
}
