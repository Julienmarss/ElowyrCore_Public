package fr.elowyr.core.commands.faction;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.util.TL; 
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class FRallyCommand extends FCommand {

    public FRallyCommand() {
        this.aliases.add("rally");
        this.aliases.add("tl");
    }

    @Override
    public void perform(CommandContext commandContext) {
        final Player player = commandContext.player;
        LCWaypoint lcWaypoint = new LCWaypoint("Rally", player.getLocation(), Color.PURPLE.asBGR(), true, true);

        if (commandContext.faction.isWilderness()) {
            player.sendMessage("§cVous n'avez pas de faction.");
            return;
        }
        if (LunarClientAPI.getInstance().isRunningLunarClient(player)) {
            for (Player player1 : commandContext.faction.getOnlinePlayers()) {
                LunarClientAPI.getInstance().removeWaypoint(player1, lcWaypoint);
                LunarClientAPI.getInstance().sendWaypoint(player1, lcWaypoint);
            }
        } else {
            player.sendMessage("§cVous n'utilisez pas Lunar-Client.");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
