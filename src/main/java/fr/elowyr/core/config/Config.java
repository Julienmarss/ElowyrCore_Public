package fr.elowyr.core.config;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.ConfigUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config
{
    private static Config instance;
    private final Plugin plugin;
    private final File file;
    private String floatFormat;
    private int killKsBc;
    private long antiSpamDelay;
    private long fishingCooldown;
    private int maxDropNetherWart;
    private int maxDropCocoa;
    private int maxDropCarrot;
    private int maxDropPotato;
    private Map<String, Long> commandCooldowns;
    private final Map<Material, Material> furnaceItems;
    private int antiCleanUpTimer;
    private String antiCleanUpTimerFormat;
    private List<String> antiCleanUpDisallowedRegions;
    private long classificationTimePlayedTaskPeriod;
    private long classificationSaveTaskPeriod;
    private int maxAp;
    public Map<Material, Integer> blocks = new HashMap<>();
    private long investTeleportationTime;
    private List<Long> investTeleportationMessageTimes;
    private String investServerName;

    public Config(final Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "config.yml");
        this.furnaceItems = new HashMap<>();

        this.blocks.put(Material.WORKBENCH, 5);
        this.blocks.put(Material.HOPPER, 5);
    }
    
    public void load() {
        if (!this.file.exists()) {
            this.plugin.saveResource("config.yml", false);
        }
        this.furnaceItems.clear();
        try {
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
            this.floatFormat = config.getString("float-format", "%.2f");
            this.killKsBc = config.getInt("kill-streak-broadcast-times", 5);
            this.antiSpamDelay = config.getLong("anti-spam.delay", 5000L);
            this.maxDropNetherWart = config.getInt("max-drop.nether-wart", 3) - 1;
            this.maxDropCocoa = config.getInt("max-drop.cocoa", 3) - 1;
            this.maxDropCarrot = config.getInt("max-drop.carrot", 3) - 1;
            this.maxDropPotato = config.getInt("max-drop.potato", 3) - 1;
            this.commandCooldowns = ConfigUtils.getStringLongMap(config.getConfigurationSection("command-cooldowns"));
            final ConfigurationSection furnaceSection = config.getConfigurationSection("furnace-items");
            if (furnaceSection != null) {
                for (final String type : furnaceSection.getKeys(false)) {
                    final Material input = BukkitUtils.getType(type);
                    if (input == null) {
                        ElowyrCore.warn("Invalid input material '" + type + "'");
                    }
                    else {
                        final Material output = BukkitUtils.getType(furnaceSection.getString(type));
                        if (output == null) {
                            ElowyrCore.warn("Invalid output material '" + furnaceSection.getString(type) + "'");
                        }
                        else {
                            this.furnaceItems.put(input, output);
                        }
                    }
                }
            }
            this.antiCleanUpTimer = config.getInt("anti-clean-up.timer", 10);
            this.antiCleanUpTimerFormat = Utils.color(config.getString("anti-clean-up.timer-format", "&c{time}s"));
            this.antiCleanUpDisallowedRegions = config.getStringList("anti-clean-up.disallowed-regions");
            this.classificationTimePlayedTaskPeriod = config.getLong("classification.tasks-period.time-played", 600L);
            this.classificationSaveTaskPeriod = config.getLong("classification.tasks-period.save", 300L);
            this.maxAp = config.getInt("max-ap", 6);
            this.investTeleportationTime = config.getLong("invest-teleportation.time", 5L);
            this.investTeleportationMessageTimes = config.getLongList("invest-teleportation.message-times");
            this.investServerName = config.getString("invest.server-name", "invest");
            ElowyrCore.info("Config loaded");
        }
        catch (Throwable ex) {
            ElowyrCore.severe("Failed to load config");
            ex.printStackTrace();
        }
    }

    public long getCommandCooldown(final String command) {
        return this.commandCooldowns.getOrDefault(command, 0L);
    }

    public static void set(final Config instance) {
        Config.instance = instance;
    }
    
    public static Config get() {
        return Config.instance;
    }

    public static Config getInstance() {
        return instance;
    }

    public static void setInstance(Config instance) {
        Config.instance = instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return file;
    }

    public String getFloatFormat() {
        return floatFormat;
    }

    public void setFloatFormat(String floatFormat) {
        this.floatFormat = floatFormat;
    }

    public int getKillKsBc() {
        return killKsBc;
    }

    public void setKillKsBc(int killKsBc) {
        this.killKsBc = killKsBc;
    }

    public long getAntiSpamDelay() {
        return antiSpamDelay;
    }

    public void setAntiSpamDelay(long antiSpamDelay) {
        this.antiSpamDelay = antiSpamDelay;
    }

    public long getFishingCooldown() {
        return fishingCooldown;
    }

    public void setFishingCooldown(long fishingCooldown) {
        this.fishingCooldown = fishingCooldown;
    }

    public int getMaxDropNetherWart() {
        return maxDropNetherWart;
    }

    public void setMaxDropNetherWart(int maxDropNetherWart) {
        this.maxDropNetherWart = maxDropNetherWart;
    }

    public int getMaxDropCocoa() {
        return maxDropCocoa;
    }

    public void setMaxDropCocoa(int maxDropCocoa) {
        this.maxDropCocoa = maxDropCocoa;
    }

    public int getMaxDropCarrot() {
        return maxDropCarrot;
    }

    public void setMaxDropCarrot(int maxDropCarrot) {
        this.maxDropCarrot = maxDropCarrot;
    }

    public int getMaxDropPotato() {
        return maxDropPotato;
    }

    public void setMaxDropPotato(int maxDropPotato) {
        this.maxDropPotato = maxDropPotato;
    }

    public Map<String, Long> getCommandCooldowns() {
        return commandCooldowns;
    }

    public void setCommandCooldowns(Map<String, Long> commandCooldowns) {
        this.commandCooldowns = commandCooldowns;
    }

    public Map<Material, Material> getFurnaceItems() {
        return furnaceItems;
    }

    public int getAntiCleanUpTimer() {
        return antiCleanUpTimer;
    }

    public void setAntiCleanUpTimer(int antiCleanUpTimer) {
        this.antiCleanUpTimer = antiCleanUpTimer;
    }

    public String getAntiCleanUpTimerFormat() {
        return antiCleanUpTimerFormat;
    }

    public void setAntiCleanUpTimerFormat(String antiCleanUpTimerFormat) {
        this.antiCleanUpTimerFormat = antiCleanUpTimerFormat;
    }

    public List<String> getAntiCleanUpDisallowedRegions() {
        return antiCleanUpDisallowedRegions;
    }

    public void setAntiCleanUpDisallowedRegions(List<String> antiCleanUpDisallowedRegions) {
        this.antiCleanUpDisallowedRegions = antiCleanUpDisallowedRegions;
    }

    public long getClassificationTimePlayedTaskPeriod() {
        return classificationTimePlayedTaskPeriod;
    }

    public void setClassificationTimePlayedTaskPeriod(long classificationTimePlayedTaskPeriod) {
        this.classificationTimePlayedTaskPeriod = classificationTimePlayedTaskPeriod;
    }

    public long getClassificationSaveTaskPeriod() {
        return classificationSaveTaskPeriod;
    }

    public void setClassificationSaveTaskPeriod(long classificationSaveTaskPeriod) {
        this.classificationSaveTaskPeriod = classificationSaveTaskPeriod;
    }

    public int getMaxAp() {
        return maxAp;
    }

    public void setMaxAp(int maxAp) {
        this.maxAp = maxAp;
    }

    public Map<Material, Integer> getBlocks() {
        return blocks;
    }

    public void setBlocks(Map<Material, Integer> blocks) {
        this.blocks = blocks;
    }

    public long getInvestTeleportationTime() {
        return investTeleportationTime;
    }

    public void setInvestTeleportationTime(long investTeleportationTime) {
        this.investTeleportationTime = investTeleportationTime;
    }

    public List<Long> getInvestTeleportationMessageTimes() {
        return investTeleportationMessageTimes;
    }

    public void setInvestTeleportationMessageTimes(List<Long> investTeleportationMessageTimes) {
        this.investTeleportationMessageTimes = investTeleportationMessageTimes;
    }

    public String getInvestServerName() {
        return investServerName;
    }

    public void setInvestServerName(String investServerName) {
        this.investServerName = investServerName;
    }
}
