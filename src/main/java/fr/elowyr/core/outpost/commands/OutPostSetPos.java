package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.Location;

public class OutPostSetPos extends TCommand {

    @Command(name = "outpost.setpos", permissionNode = "elowyrcore.event.outpost.setpos")
    public void onCommand(CommandArgs args) {
        int pos;
        if (args.length() < 2) {
            Lang.send(args.getSender(), "event.outpost.setpos.usage");
            return;
        }
        String name = args.getArgs(0);
        try {
            pos = Integer.parseInt(args.getArgs(1));
        } catch (Throwable ex) {
            Lang.send(args.getSender(), "event.outpost.setpos.invalid-position");
            return;
        }

        if (pos != 1 && pos != 2) {
            Lang.send(args.getSender(), "event.outpost.setpos.unknown-position");
            return;
        }
        OutPost outPost = OutpostsManager.get().get(name);
        if (outPost == null) {
            Lang.send(args.getSender(), "event.outpost.setpos.no-outpost", "name", name);
            return;
        }
        Location loc = (args.getPlayer()).getLocation();
        outPost.getArea().setPosition(pos, loc);
        OutpostsManager.get().updateActives();
        OutpostsManager.get().save();
        Lang.send(args.getSender(), "event.outpost.setpos.success-" + pos, "world", loc.getWorld().getName(), "x", loc.getBlockX(), "y", loc.getBlockY(), "z", loc.getBlockZ());
    }
}
