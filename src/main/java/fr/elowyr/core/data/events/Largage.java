package fr.elowyr.core.data.events;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import fr.elowyr.core.data.IEvent;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.enums.StopReason;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.listeners.events.LargageListener;
import fr.elowyr.core.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class Largage extends IEvent {

    private List<String> loots;
    private List<Location> chestLocations;
    private final File file;
    private YamlConfiguration config;
    private Plugin plugin = ElowyrCore.getInstance();
    private Location location;
    private boolean keepAlive;
    private double currentChance, maximumChance;
    private World world;
    private int blocksAroundSpawnAirdrop, chestSpawns;
    private BukkitTask timerTask;
    private boolean finished = true;
    private HashMap<Block, LCWaypoint> chestWaypoints;

    public Largage() {
        super("Largage");
        this.file = new File(ElowyrCore.getInstance().getDataFolder(), "largage.yml");
        this.loots = new ArrayList<>();
        this.chestLocations = new ArrayList<>();
        this.chestWaypoints = new HashMap<>();

        this.load();

        this.keepAlive = false;
        this.maximumChance = 100.0D;

        this.registerListener(new LargageListener(this));
    }

    public Location getRandomLocation() {
        Random random = new Random();
        int coords = blocksAroundSpawnAirdrop;
        int posX = random.nextInt(coords * 2) - coords;
        int posZ = random.nextInt(coords * 2) - coords;
        Location location = new Location(world, posX, 0, posZ);
        int posY = world.getHighestBlockYAt(location);
        location.setY(posY);
        return location;
    }

    @Override
    public void start(String name) {
        if(!this.chestLocations.isEmpty()) {
            this.chestLocations.forEach(location1 -> {
                location1.getBlock().setType(Material.AIR);
                location1.getBlock().removeMetadata("LARGAGE", plugin);
                this.chestLocations.remove(location1);
            });
        }
        for(int count = 0; count < this.chestSpawns; count++) {
            this.location = this.getRandomLocation();
            while(!this.isCorrectLocation(this.location)) {
                this.location = this.getRandomLocation();
            }

            if(this.location == null) {
                return;
            }

            this.location.getBlock().setType(Material.CHEST);
            this.location.getBlock().setMetadata("LARGAGE", new FixedMetadataValue(plugin, true));
            LCWaypoint waypoint = new LCWaypoint("Largage (" + (count + 1) + ")", location, Color.RED.asBGR(), true);
            Bukkit.getOnlinePlayers().forEach(player -> LunarClientAPI.getInstance().sendWaypoint(player, waypoint));
            this.chestWaypoints.put(this.location.getBlock(), waypoint);
            this.chestLocations.add(this.location);
        }
        this.reset();
        this.timerTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this::tick, 0, 20);
        Lang.broadcast("events.largage.start");
    }

    @Override
    public void stop(StopReason stopReason) {
        Optional.ofNullable(this.timerTask).ifPresent(BukkitTask::cancel);
        if(stopReason == StopReason.PLAYER) {
            if(!this.chestLocations.isEmpty()) {
                this.chestLocations.forEach(location1 -> {
                    location1.getBlock().setType(Material.AIR);
                    location1.getBlock().removeMetadata("LARGAGE", plugin);
                    this.chestLocations.remove(location1);
                });
            }
            Lang.broadcast("events.largage.stop.force-broadcast");
            return;
        }
        this.location = null;
        Lang.broadcast("events.largage.broadcast.stop");
    }

    @Override
    public String[] getLines() {
        return new String[]{Lang.get().getString("event.largage.title")};
    }

    /**
     * Reset the event to default values for a restart
     */
    private void reset() {
        this.finished = false;
    }

    private void load() {
        if (!this.file.exists()) {
            this.plugin.saveResource("largage.yml", false);
        }
        try {
            this.config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
            this.world = Bukkit.getWorld(this.config.getString("world"));
            this.chestSpawns = this.config.getInt("chest-spawns");
            this.blocksAroundSpawnAirdrop = this.config.getInt("blocks-around-spawn-airdrop");
            this.config.getConfigurationSection("loots").getKeys(false).forEach(key -> {
                String loot = this.config.getString("loots." + key + ".loot");
                this.loots.add(loot);
            });
            ElowyrCore.info("Largage loaded");
        }
        catch (Throwable ex) {
            ElowyrCore.severe("Failed to load Largage");
        }
    }

    private void tick() {
        this.finished = this.chestLocations.isEmpty();
        if(this.finished) {
            this.stopEvent(StopReason.NORMAL);
        }
    }

    private boolean isCorrectLocation(Location location){
        return location.getBlock().getType() == Material.AIR &&
                Board.getInstance().getFactionAt(new FLocation(location)).isNone() &&
                !RegionUtils.inArea(location, world, "spawn") &&
                RegionUtils.inAreas(location, world, "warzone", "wz1", "wz2", "wz3", "wz4") &&
                location.getY() < 104;
    }

    public List<String> getLoots() {
        return loots;
    }

    public void setLoots(List<String> loots) {
        this.loots = loots;
    }

    public List<Location> getChestLocations() {
        return chestLocations;
    }

    public void setChestLocations(List<Location> chestLocations) {
        this.chestLocations = chestLocations;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public double getCurrentChance() {
        return currentChance;
    }

    public void setCurrentChance(double currentChance) {
        this.currentChance = currentChance;
    }

    public double getMaximumChance() {
        return maximumChance;
    }

    public void setMaximumChance(double maximumChance) {
        this.maximumChance = maximumChance;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getBlocksAroundSpawnAirdrop() {
        return blocksAroundSpawnAirdrop;
    }

    public void setBlocksAroundSpawnAirdrop(int blocksAroundSpawnAirdrop) {
        this.blocksAroundSpawnAirdrop = blocksAroundSpawnAirdrop;
    }

    public int getChestSpawns() {
        return chestSpawns;
    }

    public void setChestSpawns(int chestSpawns) {
        this.chestSpawns = chestSpawns;
    }

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(BukkitTask timerTask) {
        this.timerTask = timerTask;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public HashMap<Block, LCWaypoint> getChestWaypoints() {
        return chestWaypoints;
    }

    public void setChestWaypoints(HashMap<Block, LCWaypoint> chestWaypoints) {
        this.chestWaypoints = chestWaypoints;
    }
}
