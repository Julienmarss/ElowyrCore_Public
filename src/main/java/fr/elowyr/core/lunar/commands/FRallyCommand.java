package fr.elowyr.core.lunar.commands;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.util.TL; 
import java.awt.Color;
import java.util.HashMap;

import fr.elowyr.core.lang.Lang;
import org.bukkit.Location;

public class FRallyCommand extends FCommand {
    private final HashMap<String, LCWaypoint> rallys = new HashMap<>();

    public FRallyCommand() {
        this.aliases.add("rally");
        this.aliases.add("tl");
    }

    @Override
    public void perform(CommandContext commandContext) {
        FPlayer player = commandContext.fPlayer;
        Faction faction = player.getFaction();
        if (faction.isWilderness()) {
            Lang.send(commandContext.sender, "lunar.dont-faction");
            return;
        }
        String factionId = faction.getId();
        if (this.rallys.containsKey(factionId)) {
            LCWaypoint factionRally = this.rallys.get(factionId);
            faction.getOnlinePlayers().forEach(member -> {
                if (LunarClientAPI.getInstance().isRunningLunarClient(member))
                    LunarClientAPI.getInstance().removeWaypoint(member, factionRally);
                Lang.send(member, "lunar.rally.remove", "player", player.getName());
            });
            this.rallys.remove(factionId);
        } else {
            Location location = player.getPlayer().getLocation();
            LCWaypoint newRally = new LCWaypoint("Rally " + player.getName(), location, (new Color(255, 0, 0)).getRGB(), true, true);
            faction.getOnlinePlayers().forEach(member -> {
                if (LunarClientAPI.getInstance().isRunningLunarClient(member))
                    LunarClientAPI.getInstance().sendWaypoint(member, newRally);
                Lang.send(member, "lunar.rally.set", "player", player.getName());
            });
            this.rallys.put(factionId, newRally);
        }
    }

    public TL getUsageTranslation() {
        return null;
    }
}
