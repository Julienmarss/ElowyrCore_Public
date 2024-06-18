package fr.elowyr.core.throne;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.ConfigUtils;
import fr.elowyr.core.utils.world.BukkitLocation;
import fr.elowyr.core.utils.world.SimpleArea;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ThroneManager {

    private static ThroneManager instance;
    private final Plugin plugin;
    private final File file;
    private SimpleArea zone;
    private List<BukkitLocation> blockLocations;
    private Player currentOwner;
    private final List<Player> playersInZone;
    private long taskRefresh;
    private double rewardMoney;
    
    public ThroneManager(final Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "throne.yml");
        this.playersInZone = new LinkedList<>();
    }

    public void load() {
        if (!this.file.exists()) {
            this.plugin.saveResource("throne.yml", false);
        }
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(this.file);
        this.rewardMoney = config.getDouble("reward-money", 200.0);
        this.taskRefresh = config.getLong("task-refresh", 100L);
        this.zone = SimpleArea.from(config.getConfigurationSection("zone"));
        this.blockLocations = ConfigUtils.getSectionList(config, "block-locations").map(BukkitLocation::from).collect(Collectors.toList());
        ElowyrCore.info("Throne loaded");
    }
    
    public void checkPlayers() {
        final int size = this.playersInZone.size();
        final Player newCurrentOwner = (size == 1) ? this.playersInZone.get(0) : null;
        if (newCurrentOwner != this.currentOwner) {
            if (this.currentOwner != null) {
                Lang.send(this.currentOwner, "listeners.throne.capturing.stop");
            }
            if (newCurrentOwner != null) {
                Lang.send(newCurrentOwner, "listeners.throne.capturing.start");
            }
            this.currentOwner = newCurrentOwner;
            final Material type = (size == 1) ? Material.GOLD_BLOCK : ((size == 0) ? Material.IRON_BLOCK : Material.COAL_BLOCK);
            this.setBlocks(type);
        }
    }
    
    private void setBlocks(final Material type) {
        for (final BukkitLocation loc : this.blockLocations) {
            loc.toLocation().getBlock().setType(type);
        }
    }
    
    public static void set(final ThroneManager instance) {
        ThroneManager.instance = instance;
    }
    
    public static ThroneManager get() {
        return ThroneManager.instance;
    }

    public static ThroneManager getInstance() {
        return instance;
    }

    public static void setInstance(ThroneManager instance) {
        ThroneManager.instance = instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return file;
    }

    public SimpleArea getZone() {
        return zone;
    }

    public void setZone(SimpleArea zone) {
        this.zone = zone;
    }

    public List<BukkitLocation> getBlockLocations() {
        return blockLocations;
    }

    public void setBlockLocations(List<BukkitLocation> blockLocations) {
        this.blockLocations = blockLocations;
    }

    public Player getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(Player currentOwner) {
        this.currentOwner = currentOwner;
    }

    public List<Player> getPlayersInZone() {
        return playersInZone;
    }

    public long getTaskRefresh() {
        return taskRefresh;
    }

    public void setTaskRefresh(long taskRefresh) {
        this.taskRefresh = taskRefresh;
    }

    public double getRewardMoney() {
        return rewardMoney;
    }

    public void setRewardMoney(double rewardMoney) {
        this.rewardMoney = rewardMoney;
    }
}
