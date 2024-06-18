package fr.elowyr.core.managers;

import com.massivecraft.factions.cmd.FCmdRoot;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.ap.APManager;
import fr.elowyr.core.ap.commands.*;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.classement.commands.*;
import fr.elowyr.core.commands.*;
import fr.elowyr.core.commands.events.EventListCommand;
import fr.elowyr.core.commands.events.EventStartCommand;
import fr.elowyr.core.commands.events.EventStopCommand;
import fr.elowyr.core.commands.faction.FFlyCommand;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.expansions.*;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.items.commands.ItemsAdminCommands;
import fr.elowyr.core.items.commands.ItemsCommand;
import fr.elowyr.core.items.commands.ItemsGiveCommand;
import fr.elowyr.core.items.commands.ItemsHelpCommand;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.listeners.*;
import fr.elowyr.core.listeners.events.EventsListener;
import fr.elowyr.core.lunar.LunarManager;
import fr.elowyr.core.lunar.commands.FRallyCommand;
import fr.elowyr.core.lunar.commands.LunarCommand;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.outpost.commands.*;
import fr.elowyr.core.scheduler.SchedulerManager;
import fr.elowyr.core.tasks.*;
import fr.elowyr.core.throne.ThroneManager;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.user.commands.boost.BoostAddCommand;
import fr.elowyr.core.user.commands.boost.BoostCommand;
import fr.elowyr.core.user.commands.flytime.FlyTimeAddCommand;
import fr.elowyr.core.user.commands.flytime.FlyTimeCommand;
import fr.elowyr.core.user.commands.flytime.FlyTimeRemoveCommand;
import fr.elowyr.core.user.commands.flytime.FlyTimeSetCommand;
import fr.elowyr.core.user.commands.votecount.VoteCountAddCommand;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.menu.MenuManager;
import fr.elowyr.core.voteparty.VoteManager;
import fr.elowyr.core.voteparty.commands.VoteAddCommand;
import fr.elowyr.core.voteparty.commands.VoteCommand;
import fr.elowyr.core.voteparty.commands.VoteSetGoalCommand;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Manager {

    public static final Queue<UUID> FARM_ARMOR_EQUIPPED = new ConcurrentLinkedQueue<>();
    public static final Map<UUID, UUID>  KILLERS = new ConcurrentHashMap<>();
    public static final Map<UUID, BukkitTask> INVEST_TELEPORTATION = new ConcurrentHashMap<UUID, BukkitTask>();
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void init(final Plugin plugin) throws Throwable {
        Config.set(new Config(plugin));
        Config.get().load();
        Lang.set(new Lang(plugin));
        Lang.get().load();
        DataManager.set(new DataManager(plugin));
        DataManager.get().load();
        LunarManager.set(new LunarManager(plugin));
        LunarManager.get().load();
        GlobalClassificationManager.set(new GlobalClassificationManager());
        GlobalClassificationManager.get().load();
        SchedulerManager.set(new SchedulerManager(plugin));
        SchedulerManager.get().load();
        VoteManager.set(new VoteManager(plugin));
        VoteManager.get().load();
        ThroneManager.set(new ThroneManager(plugin));
        ThroneManager.get().load();
        OutpostsManager.set(new OutpostsManager(plugin));
        OutpostsManager.get().load();
        APManager.set(new APManager());
        APManager.get().load();
        MenuManager.getInstance().register(ElowyrCore.getInstance());
        //new ScoreboardManager();
        new EventsManager();
        new ItemsManager();
        new FarmArmorTask().runTaskTimerAsynchronously(plugin, 0L, 20L);
        new FlyTimeTask().runTaskTimerAsynchronously(plugin, 0L, 20L);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new HarvesterTask(), 20, (20*5));
        (new ThroneTask()).runTaskTimer(plugin, 0L, ThroneManager.get().getTaskRefresh());
        new ClassificationTimePlayedTask().runTaskTimerAsynchronously(plugin, 0L, Config.get().getClassificationTimePlayedTaskPeriod() * 20L);
        new ClassificationSaveTask().runTaskTimerAsynchronously(plugin, 0L, Config.get().getClassificationSaveTaskPeriod() * 20L);
        FCmdRoot.getInstance().addSubCommand(new FRallyCommand());
        FCmdRoot f = FCmdRoot.getInstance();
        f.subCommands.remove(f.cmdFly);
        f.addSubCommand(new FFlyCommand());
        f.addSubCommand(new FRallyCommand());

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    public static void postInit() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            DbUtils.loadUser(player.getUniqueId(), user -> {
                if (user == null) {
                    user = new User(player.getUniqueId(), player.getName());
                    DbUtils.insertUser(user);
                }
                else {
                    user.setPlayer(player);
                }
                UserManager.addUser(user);
                UserManager.setOnline(user, player, true);
            });
        }
    }
    
    public static List<Listener> getListeners() {
        return Arrays.asList(new PlayerListener(), new LunarListener(), new ClassificationListener(), new ThroneListener(), new UtilListener(), new BoostListener(),
                new UsableItemListener(), new ThroneListener(), new FactionOutPostListener(), new EventsListener(), new APListener());
    }
    
    public static List<TCommand> getCommands() {
        return Arrays.asList(
                new ApsCommand(), new ApsResetCommand(), new ApsHelpCommand(), new ApsInfoCommand(), new ApsRemoveCommand(),
                new ApsAddCommand(), new ElowyrReloadCommand(), new LunarCommand(), new PubCommand(), new FurnaceCommand(),
                new BoostCommand(), new BoostAddCommand(), new ItemsGiveCommand(), new ItemsCommand(), new ItemsHelpCommand(),
                new ItemsAdminCommands(), new FlyTimeSetCommand(), new FlyTimeRemoveCommand(), new FlyTimeCommand(),
                new FlyTimeAddCommand(), new VoteCommand(), new VoteAddCommand(), new VoteSetGoalCommand(),
                new VoteCountAddCommand(), new RefillCommand(), new EventListCommand(), new EventStopCommand(), new EventStartCommand(),
                new OutPostCreateCommand(), new OutpostCommand(), new OutPostListCommand(), new OutPostInfoCommand(),
                new OutPostSetPos(), new OutPostStartCommand(), new OutPostStopCommand(), new ClassificationResetCommand(),
                new ClassificationRemovePlayerCommand(), new ClassificationRemoveFactionCommand(), new ClassificationGivePlayerCommand(),
                new ClassificationGiveFactionCommand(), new ClassificationForceSaveCommand(), new ClassificationCommand(), new GrainsCommand());
    }
    
    public static List<PlaceholderExpansion> getExpansions() {
        return Arrays.asList(new CombatExpansion(), new ClassificationExpansion(), new FlyTimeExpansion(), new VotePartyExpansion(), new HearthExpansion()
                , new OutPostExpansion(), new VotesExpansion());
    }
    
    public static void stop() {
        commonStop();
        GlobalClassificationManager.get().saveSync((byte)3);
        UserManager.saveAll();
        if (DataManager.get() != null) {
            DataManager.get().disconnect();
        }
    }
    
    public static void reload() {
        commonStop();
        Config.get().load();
        Lang.get().load();
        DataManager.get().load();
        SchedulerManager.get().stop();
        ThroneManager.get().load();
        SchedulerManager.get().load();
        ItemsManager.getInstance().loadItems();
    }

    private static void commonStop() {
        SchedulerManager.get().stop();
        ItemsManager.getInstance().clear();
    }
    
    public static void sendToServer(final Player player, final String server) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (final DataOutputStream out = new DataOutputStream(buffer)) {
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(ElowyrCore.getInstance(), "BungeeCord", buffer.toByteArray());
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            Lang.send(player, "send-to-server-failed", "server", server);
        }
    }
}
