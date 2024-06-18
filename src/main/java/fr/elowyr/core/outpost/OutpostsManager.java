package fr.elowyr.core.outpost;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.listeners.events.OutPostListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class OutpostsManager {

    private static OutpostsManager instance;

    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;

    private BukkitTask saveTask;
    private boolean enabled;

    private List<Integer> broadcastPercentages;
    private final Map<String, OutPost> outPosts;
    private final List<OutPost> activeOutPosts;
    private long captureTaskPeriod;

    public OutpostsManager(final Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "outposts.yml");
        this.activeOutPosts = new LinkedList<>();
        this.outPosts = new HashMap<>();
        this.enabled = false;
    }

    public void load() {
        if (!this.file.exists())
            this.plugin.saveResource("outposts.yml", false);
        this.outPosts.clear();
        this.activeOutPosts.clear();
        try {
            this.config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(
                    Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
            this.broadcastPercentages = this.config.getIntegerList("broadcast-percentages");
            this.captureTaskPeriod = this.config.getLong("capture-task-period");
            ConfigurationSection outPostList = this.config.getConfigurationSection("outpost-list");
            if (outPostList != null)
                for (String name : outPostList.getKeys(false)) {
                    OutPost outPost = OutPost.from(name, outPostList.getConfigurationSection(name));
                    if (outPost.isValid())
                        this.activeOutPosts.add(outPost);
                    this.outPosts.put(name, outPost);
                }
            updateActives();
            this.activeOutPosts.forEach(OutPost::stop);
            ElowyrCore.info("OutPost loaded");
        } catch (Throwable ex) {
            ElowyrCore.severe("Failed to load outpost");
            ex.printStackTrace();
            this.config = new YamlConfiguration();
        }
    }

    public void add(OutPost outPost) {
        this.outPosts.put(outPost.getName(), outPost);
    }

    public OutPost get(String name) {
        return this.outPosts.get(name);
    }

    public OutPost[] getOutPosts(String factionId) {
        return this.activeOutPosts.stream()
                .filter(outPost -> outPost.getStatus().isOwner(factionId))
                .toArray(OutPost[]::new);
    }

    public Collection<OutPost> getOutPosts() {
        return this.outPosts.values();
    }

    public List<OutPost> getActiveOutPosts() {
        return this.activeOutPosts;
    }

    public long getCaptureTaskPeriod() {
        return this.captureTaskPeriod;
    }

    public List<Integer> getBroadcastPercentages() {
        return this.broadcastPercentages;
    }

    public void start() {
        if (!this.enabled) {
            Bukkit.getPluginManager().registerEvents(OutPostListener.INSTANCE, this.plugin);
            this.enabled = true;
            this.activeOutPosts.forEach(OutPost::start);
            Lang.broadcast("event.outpost.start.broadcast");
        }
    }

    public void stop(boolean now) {
        if (this.enabled) {
            HandlerList.unregisterAll(OutPostListener.INSTANCE);
            this.activeOutPosts.forEach(OutPost::stop);
            if (now) {
                saveNow();
            } else {
                save();
            }
            this.enabled = false;
            Lang.broadcast("event.outpost.stop.broadcast");
        }
    }

    public void updateActives() {
        this.activeOutPosts.clear();
        for (OutPost outPost : this.outPosts.values()) {
            if (outPost.isValid()) {
                this.activeOutPosts.add(outPost);
                if (this.enabled)
                    outPost.start();
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void save() {
        if (this.saveTask == null)
            this.saveTask = Bukkit.getScheduler().runTaskLater(ElowyrCore.getInstance(), this::saveNow, 100L);
    }

    void saveNow() {
        try {
            if (this.saveTask == null)
                return;
            this.saveTask.cancel();
            this.saveTask = null;
            for (OutPost outpost : this.outPosts.values())
                outpost.write(this.config.createSection("outpost-list." + outpost.getName()));
            String data = this.config.saveToString();
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(this.file.toPath()), StandardCharsets.UTF_8)) {
                writer.write(data);
            }
            ElowyrCore.info("Outposts saved");
        } catch (Throwable ex) {
            ElowyrCore.severe("Failed to save outposts");
            ex.printStackTrace();
        }
    }

    public static void set(OutpostsManager instance) {
        OutpostsManager.instance = instance;
    }

    public static OutpostsManager get() {
        return instance;
    }

    public static OutpostsManager getInstance() {
        return instance;
    }

    public static void setInstance(OutpostsManager instance) {
        OutpostsManager.instance = instance;
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

    public BukkitTask getSaveTask() {
        return saveTask;
    }

    public void setSaveTask(BukkitTask saveTask) {
        this.saveTask = saveTask;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setBroadcastPercentages(List<Integer> broadcastPercentages) {
        this.broadcastPercentages = broadcastPercentages;
    }

    public void setCaptureTaskPeriod(long captureTaskPeriod) {
        this.captureTaskPeriod = captureTaskPeriod;
    }
}
