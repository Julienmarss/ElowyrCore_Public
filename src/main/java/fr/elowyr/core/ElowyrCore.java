package fr.elowyr.core;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Relation;
import fr.elowyr.core.listeners.StartingListener;
import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.tasks.ResetVoteCountTask;
import fr.elowyr.core.utils.commands.CommandFramework;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.List;

public class ElowyrCore extends JavaPlugin {
    private static ElowyrCore instance;

    private CommandFramework commandFramework = new CommandFramework(this);

    private List<String> commands;
    private int taskID;

    @Override
    public void onEnable() {
        instance = this;
        final PluginManager pluginManager = this.getServer().getPluginManager();
        info("Server is starting...");
        pluginManager.registerEvents(StartingListener.INSTANCE, this);
        commands = getConfig().getStringList("blocked");
        try {
            Manager.init(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Manager.getCommands().forEach(commandFramework::registerCommands);
        Manager.getListeners().forEach(listener -> pluginManager.registerEvents(listener, this));
        resetCount();
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            Manager.getExpansions().forEach(PlaceholderExpansion::register);
        }
        Manager.postInit();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            HandlerList.unregisterAll(StartingListener.INSTANCE);
            info("This plugin is enabled");
        }, 100L);
    }

    public void resetCount() {
        if (isCurrentDate(23, 59, 50)) {
            taskID = Bukkit.getServer().getScheduler().runTaskTimer(ElowyrCore.getInstance(), new ResetVoteCountTask(), 0, 20).getTaskId();
        }
    }

    @Override
    public void onDisable() {
        Manager.stop();
    }

    public static boolean isCurrentDate(int hours, int minutes, int seconds) {
        Calendar now = Calendar.getInstance();
        int currentHours = now.get(11);
        int currentMinutes = now.get(12);
        int currentSeconds = now.get(13);
        return currentHours == hours && currentMinutes == minutes && currentSeconds == seconds;
    }

    public boolean nearEnemy(Location location, Faction faction) {
        for (Player current : location.getWorld().getPlayers()) {
            if (current.hasPermission("fly.bypass") || FPlayers.getInstance().getByPlayer(current).getFaction().getRelationTo(faction) != Relation.ENEMY &&
                    !FPlayers.getInstance().getByPlayer(current).getFaction().isWilderness()) {
                continue;
            }

            if (location.distance(current.getLocation()) <= 25) {
                return true;
            }
        }
        return false;
    }

    public boolean isIn(Player player, Faction faction) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        return faction.getFPlayers().contains(fPlayer) || faction.getRelationWish(fPlayer.getFaction()) == Relation.ALLY;
    }

    /**
     * Log a info message.
     */
    public static void info(final String message) {
        instance.getLogger().info(message);
    }

    /**
     * Log a warning message.
     */
    public static void warn(final String message) {
        instance.getLogger().warning(message);
    }

    /**
     * Log a severe message.
     */
    public static void severe(final String message) {
        instance.getLogger().severe(message);
    }

    public static ElowyrCore getInstance() {
        return instance;
    }

    public static void setInstance(ElowyrCore instance) {
        ElowyrCore.instance = instance;
    }

    public CommandFramework getCommandFramework() {
        return commandFramework;
    }

    public void setCommandFramework(CommandFramework commandFramework) {
        this.commandFramework = commandFramework;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
}
