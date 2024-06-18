package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.api.event.outpost.OutPostEvent;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

public class OutPostCreateCommand extends TCommand {


    @Override
    @Command(name = "outpost.create", permissionNode = "elowyrcore.event.outpost.create")
    public void onCommand(CommandArgs args) {
        if (args.length() == 0) {
            Lang.send(args.getSender(), "event.outpost.create.usage");
            return;
        }
        OutpostsManager manager = OutpostsManager.get();
        String name = args.getArgs(0);
        if (manager.get(name) != null) {
            Lang.send(args.getSender(), "event.outpost.create.already-exist", "name", name);
            return;
        }
        OutPost outPost = new OutPost(name);
        if ((new OutPostEvent(args.getSender(), outPost, OutPostEvent.Action.CREATE)).run()) {
            Lang.send(args.getSender(), "event.outpost.create.cancelled");
            return;
        }
        manager.add(outPost);
        manager.save();
        Lang.send(args.getSender(), "event.outpost.create.success", "name", name);
    }
}
