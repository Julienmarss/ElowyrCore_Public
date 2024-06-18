package fr.elowyr.core.voteparty;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.tasks.VoteSaveTask;
import fr.elowyr.core.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;

public class VoteManager {
    private static VoteManager instance;

    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;

    private String subtitle;
    private String vipSubtitle;
    private int serverGoal;
    private int serverCurrent;
    private List<String> serverRewards;
    private List<String> serverRewardsvip;

    private boolean modified;
    private BukkitTask saveTask;

    public VoteManager(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "votes.yml");
    }

    public void load() {
        if (!this.file.exists())
            ElowyrCore.getInstance().saveResource("votes.yml", false);
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.subtitle = this.config.getString("subtitle");
        this.vipSubtitle = this.config.getString("vip-subtitle");
        this.serverGoal = this.config.getInt("server.goal", 300);
        this.serverCurrent = this.config.getInt("server.current", 0);
        this.serverRewards = this.config.getStringList("server.rewards");
        this.serverRewardsvip = this.config.getStringList("server.vip-rewards");
        ElowyrCore.info("Votes loaded");
    }

    public void setServerCurrent(int serverCurrent, int amount) {
        if (serverCurrent >= serverGoal) {

            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.hasPermission("votepartyx2.double")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + players.getName() + " 300000");
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + players.getName() + " 150000");
                }
                BukkitUtils.sendTitle(players, "§e§l⦙ §6§lVoteParty", "§fFélicitation, vous avez atteint le §aVoteParty  §f!", 20, 50, 2);
            }
            this.serverCurrent = 0;

        } else {
            this.serverCurrent = this.serverCurrent + amount;
        }

        config.set("server.current", serverCurrent);
        modified = true;

        if ((saveTask == null))
            saveTask = new VoteSaveTask().runTaskLaterAsynchronously(ElowyrCore.getInstance(), 10L * 20L);
    }

    public void reset(int serverCurrent) {
        this.serverCurrent = serverCurrent;
        this.config.set("server.current", serverCurrent);
        this.modified = true;
        if (this.saveTask == null)
            this.saveTask = (new VoteSaveTask()).runTaskLaterAsynchronously(ElowyrCore.getInstance(), 200L);
    }

    public int getServerCurrent() {
        return this.serverCurrent;
    }

    public void setServerGoal(int serverGoal) {
        this.serverGoal = serverGoal;
        this.config.set("server.goal", serverGoal);
        this.modified = true;
        save();
    }

    public void save() {
        if (!this.modified)
            return;
        try {
            if (!this.file.exists() &&
                    this.file.getParentFile().mkdirs())
                this.file.createNewFile();
            this.config.save(this.file);
            this.modified = false;
            if (this.saveTask != null)
                this.saveTask.cancel();
            this.saveTask = null;
            ElowyrCore.info("Votes saved");
        } catch (Throwable ex) {
            ElowyrCore.info("Failed to save votes");
            ex.printStackTrace();
        }
    }

    public static void set(VoteManager instance) {
        VoteManager.instance = instance;
    }

    public static VoteManager get() {
        return instance;
    }

    public int getServerGoal() {
        return serverGoal;
    }

    public static VoteManager getInstance() {
        return instance;
    }

    public static void setInstance(VoteManager instance) {
        VoteManager.instance = instance;
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getVipSubtitle() {
        return vipSubtitle;
    }

    public void setVipSubtitle(String vipSubtitle) {
        this.vipSubtitle = vipSubtitle;
    }

    public void setServerCurrent(int serverCurrent) {
        this.serverCurrent = serverCurrent;
    }

    public List<String> getServerRewards() {
        return serverRewards;
    }

    public void setServerRewards(List<String> serverRewards) {
        this.serverRewards = serverRewards;
    }

    public List<String> getServerRewardsvip() {
        return serverRewardsvip;
    }

    public void setServerRewardsvip(List<String> serverRewardsvip) {
        this.serverRewardsvip = serverRewardsvip;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public BukkitTask getSaveTask() {
        return saveTask;
    }

    public void setSaveTask(BukkitTask saveTask) {
        this.saveTask = saveTask;
    }
}
