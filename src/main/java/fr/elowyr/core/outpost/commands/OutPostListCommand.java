package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;

import java.util.Collection;

public class OutPostListCommand extends TCommand {

    @Command(name = "outpost.list", permissionNode = "elowyrcore.event.outpost.list")
    public void onCommand(CommandArgs args) {
        Collection<OutPost> outPosts = OutpostsManager.get().getOutPosts();
        if (outPosts.isEmpty()) {
            Lang.send(args.getSender(), "event.outpost.list.empty");
        } else {
            Lang.send(args.getSender(), "event.outpost.list.header", "size", outPosts.size());
            for (OutPost outPost : outPosts) {
                Lang.send(args.getSender(), "event.outpost.list.entry", "name", outPost.getName());
            }
        }
    }
}
