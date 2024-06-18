package fr.elowyr.core.data.events;

import com.massivecraft.factions.FPlayer;
import fr.elowyr.core.outpost.data.Free;
import fr.elowyr.core.outpost.data.OutPostStatus;
import fr.elowyr.core.utils.world.SimpleArea;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OutPost {
    private final String name;

    private final SimpleArea area;

    private List<String> rewardCommands;

    private long rewardPeriod;

    private OutPostStatus status;

    private final Set<FPlayer> playersInArea;

    public OutPost(String name) {
        this(name, new SimpleArea(), null, 0L);
        this.status = new Free(this);
    }

    private OutPost(String name, SimpleArea area, List<String> rewardCommands, long rewardPeriod) {
        this.name = name;
        this.area = area;
        this.rewardCommands = rewardCommands;
        this.rewardPeriod = rewardPeriod;
        this.status = new Free(this);
        this.playersInArea = new HashSet<>();
    }

    public String getName() {
        return this.name;
    }

    public SimpleArea getArea() {
        return this.area;
    }

    public synchronized void addReward(String reward) {
        if (this.rewardCommands == null)
            this.rewardCommands = new LinkedList<>();
        this.rewardCommands.add(reward);
    }

    public synchronized String removeReward(int index) {
        String reward = null;
        if (this.rewardCommands != null) {
            reward = this.rewardCommands.remove(index);
            if (this.rewardCommands.isEmpty())
                this.rewardCommands = null;
        }
        return reward;
    }

    public List<String> getRewardCommands() {
        return this.rewardCommands;
    }

    public void setRewardPeriod(long rewardPeriod) {
        this.rewardPeriod = rewardPeriod;
        this.status.onRewardPeriodUpdated();
    }

    public long getRewardPeriod() {
        return this.rewardPeriod;
    }

    public void setStatus(OutPostStatus status) {
        if (this.status != status)
            this.status = status.onPlayersEnter(this.playersInArea);
    }

    public OutPostStatus getStatus() {
        return this.status;
    }

    public boolean isValid() {
        return this.area.isDefined();
    }

    public void free() {
        setStatus(new Free(this));
    }

    public void enter(FPlayer fp) {
        if (this.playersInArea.add(fp))
            setStatus(this.status.onPlayerEnter(fp));
    }

    public void exit(FPlayer fp) {
        if (this.playersInArea.remove(fp))
            setStatus(this.status.onPlayerExit(fp));
    }

    public Set<FPlayer> getPlayersInArea() {
        return this.playersInArea;
    }

    public void start() {
        this.status.start();
    }

    public void stop() {
        this.playersInArea.clear();
        this.status.stop();
        this.status = new Free(this);
    }

    public void write(ConfigurationSection section) {
        this.area.write(section.createSection("area"));
        section.set("rewards", this.rewardCommands);
        section.set("reward-period", this.rewardPeriod);
    }

    public static OutPost from(String name, ConfigurationSection section) {
        SimpleArea area = SimpleArea.from(section.getConfigurationSection("area"));
        List<String> rewards = section.getStringList("rewards");
        long rewardPeriod = section.getLong("reward-period");
        return new OutPost(name, area, rewards, rewardPeriod);
    }

    public void setRewardCommands(List<String> rewardCommands) {
        this.rewardCommands = rewardCommands;
    }
}