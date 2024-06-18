package fr.elowyr.core.lunar;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.tasks.TeamViewUpdaterThread;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class LunarManager {

    private static LunarManager instance;
    private final Set<LCWaypoint> waypoints = new HashSet<>();
    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;

    public LunarManager(final Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "lunar.yml");

        new TeamViewUpdaterThread().start();
    }

    public void load() {
        if (!this.file.exists()) {
            this.plugin.saveResource("lunar.yml", false);
        }
        try {
            this.config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
            final ConfigurationSection list = this.config.getConfigurationSection("waypoints");
            for (String key : list.getKeys(false)) {
                int color = new Color(list.getInt(key + ".R"), list.getInt(key + ".G"), list.getInt(key + "B")).getRGB();
                LCWaypoint newWaypoint = new LCWaypoint(list.getString(key + ".name")
                        , new Location(Bukkit.getWorld(list.getString(key + ".world")), list.getDouble(key + ".x"), list.getDouble(key + ".y"), list.getDouble(key + ".z")), color, true);
                this.waypoints.add(newWaypoint);
            }

        } catch (Throwable ex) {
            ElowyrCore.severe("Failed to load lunar configuration.");
            ex.printStackTrace();
            this.config = new YamlConfiguration();
        }
    }

    public static void set(final LunarManager instance) {
        LunarManager.instance = instance;
    }

    public static LunarManager get() {
        return LunarManager.instance;
    }

    public static LunarManager getInstance() {
        return instance;
    }

    public static void setInstance(LunarManager instance) {
        LunarManager.instance = instance;
    }

    public Set<LCWaypoint> getWaypoints() {
        return waypoints;
    }

    public Plugin getPlugin() {
        return plugin;
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
}
