package fr.elowyr.core.outpost.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.TimeUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import fr.elowyr.core.utils.world.BukkitLocation;
import fr.elowyr.core.utils.world.SimpleArea;
import org.bukkit.command.CommandSender;

import java.util.List;

public class OutPostInfoCommand extends TCommand {

    @Override
    @Command(name = "outpost.info", permissionNode = "elowyrcore.event.outpost.info")
    public void onCommand(CommandArgs args) {
        if (args.length() == 0) {
            Lang.send(args.getSender(), "event.outpost.info.usage");
            return;
        }
        String name = args.getArgs(0);
        OutPost outPost = OutpostsManager.get().get(name);
        String base = "event.outpost.info.";
        if (outPost == null) {
            Lang.send(args.getSender(), base + "no-outpost", "name", name);
            return;
        }
        SimpleArea area = outPost.getArea();
        List<String> rewards = outPost.getRewardCommands();
        Lang.send(args.getSender(), base + "name", "name", outPost.getName());
        Lang.send(args.getSender(), base + (outPost.isValid() ? "valid" : "invalid"));
        Lang.send(args.getSender(), base + "reward-period", "time", TimeUtils.format(outPost.getRewardPeriod() / 20L));
        sendLocation(args.getSender(), base + "min", area.getMin());
        sendLocation(args.getSender(), base + "max", area.getMax());
        if (rewards == null || rewards.isEmpty()) {
            Lang.send(args.getSender(), base + "rewards.empty");
        } else {
            Lang.send(args.getSender(), base + "rewards.header", "size", rewards.size());
            for (String reward : rewards) {
                Lang.send(args.getSender(), base + "rewards.entry", "command", reward);
            }
        }
    }

    private void sendLocation(CommandSender sender, String key, BukkitLocation loc) {
        if (loc == null) {
            Lang.send(sender, key + ".undefined");
        } else {
            Lang.send(sender, key + ".location", "world", loc.getWorldName(), "x", loc.getX(), "y", loc.getY(), "z",
                    loc.getZ());
        }
    }
}
