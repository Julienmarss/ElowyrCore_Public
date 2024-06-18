package fr.elowyr.core.lunar.commands;

import com.lunarclient.bukkitapi.LunarClientAPI;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class LunarCommand extends TCommand {

    @Command(name = "lunar", permissionNode = "elowyrcore.lunar")
    public void onCommand(CommandArgs args) {
        if (args.length() == 1) {
            Player target = Bukkit.getPlayer(args.getArgs(0));
            if (target == null) {
                Lang.send(args.getPlayer(),"lunar.dont-player", "player", target.getName());
                return;
            }
            if (LunarClientAPI.getInstance().isRunningLunarClient(target)) {
                Lang.send(args.getPlayer(), "lunar.playing", "player", target.getName());
            } else {
                Lang.send(args.getPlayer(), "lunar.dont-playing", "player", target.getName());
            }
            return;
        }
        Set<Player> playersSet = LunarClientAPI.getInstance().getPlayersRunningLunarClient();
        String onLunar = playersSet.stream().map(OfflinePlayer::getName).collect(Collectors.joining(", "));
        Lang.send(args.getPlayer(), "lunar.check-all", "allname", onLunar, "allnumber", playersSet.size());
    }

}
