package fr.elowyr.core.classement.commands;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.utils.TimeUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.*;

public class ClassificationForceSaveCommand extends TCommand {
    
    @Override
    @Command(name = "classification.force-save", permissionNode = "elowyrcore.classification.forcesave")
    public void onCommand(CommandArgs args) {
        byte type = 3;
        final String name = args.getArgs(0).toLowerCase();
        if ("factions".startsWith(name)) {
            type = 1;
        }
        else {
            if (!"users".startsWith(name)) {
                Lang.send(args.getSender(), "classification.force-save.unknown-type", "type", name);
                return;
            }
            type = 2;
        }
        final byte finalType = type;
        Bukkit.getScheduler().runTaskAsynchronously(ElowyrCore.getInstance(), () -> {
            long start = System.currentTimeMillis();
            int changes = GlobalClassificationManager.get().saveSync(finalType);
            long elapsed = System.currentTimeMillis() - start;
            Lang.send(args.getSender(), "classification.force-save.success", "time", TimeUtils.formatMS(elapsed), "changes", changes);
        });
    }
}
