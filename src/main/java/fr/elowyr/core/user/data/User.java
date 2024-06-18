package fr.elowyr.core.user.data;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.data.Boost;
import fr.elowyr.core.data.BoostType;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class User {

    private Long id;
    private final UUID uniqueId;
    private String username;
    private int serialKill;
    private long boostdelay;
    private int votes;
    private int flyTime;
    private boolean flyTimeChanged;
    private long lastClassificationUpdate;
    private Map<BoostType, List<Boost>> boosts;
    private Map<String, Long> commandCooldowns;
    private Player player;

    public User(final UUID uniqueId, final String username) {
        this(null, uniqueId, username, 0, 0, 0, 0);
    }

    public User(final Long id, final UUID uniqueId, final String username, final int serialKill, final int flyTime, final long boostdelay, final int votes) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.username = username;
        this.serialKill = serialKill;
        this.flyTime = flyTime;
        this.boostdelay = boostdelay;
        this.commandCooldowns = new HashMap<>();
        this.boosts = null;
        this.votes = votes;
    }

    public void addFlyTime(final int time) {
        this.flyTime += time;
    }
    
    public void addBoost(final Boost boost) {
        if (this.boosts == null) {
            this.boosts = new EnumMap<>(BoostType.class);
        }
        final List<Boost> boostList = this.boosts.computeIfAbsent(boost.getType(), k -> new LinkedList());
        boostList.add(boost);
        Collections.sort(boostList);
    }
    
    public Boost getBestBoost(final BoostType type) {
        if (this.boosts == null) {
            return null;
        }
        final List<Boost> boostList = this.boosts.get(type);
        return (boostList == null || boostList.isEmpty()) ? null : boostList.get(0);
    }
    
    public Boost getBoost(final BoostType type, final int value) {
        if (this.boosts == null) {
            return null;
        }
        final List<Boost> boostList = this.boosts.get(type);
        if (boostList == null) {
            return null;
        }
        return boostList.stream().filter(boost -> boost.getValue() == value).findFirst().orElse(null);
    }
    
    public Stream<Boost> getBoosts() {
        if (this.boosts == null) {
            return Stream.empty();
        }
        return this.boosts.values().stream().flatMap((Function<? super List<Boost>, ? extends Stream<? extends Boost>>)Collection::stream);
    }

    public boolean canUseCommand(final String command, final boolean message) {
        final Long last = this.commandCooldowns.get(command);
        final long now = System.currentTimeMillis();
        final long remaining = (last == null) ? 0L : (Config.get().getCommandCooldown(command) - (now - last));
        if (remaining > 0L) {
            if (message) {
                Lang.send(this.player, "cooldown", "time", TimeUtils.formatMS(remaining));
            }
            return false;
        }
        this.commandCooldowns.put(command, now);
        return true;
    }
    
    public void save(final boolean async) {
        if (!this.flyTimeChanged) {
            return;
        }
        final Runnable task = () -> DbUtils.updateUserSync(this, "fly_time", this.flyTime);
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(ElowyrCore.getInstance(), task);
        }
        else {
            task.run();
        }
    }
    
    public List<Boost> getFinishedBoosts() {
        if (this.boosts == null) {
            return null;
        }
        return this.getBoosts().filter(Boost::isFinished).collect(Collectors.toList());
    }
    
    public void removeBoost(final Boost boost) {
        final List<Boost> boostList = this.boosts.get(boost.getType());
        if (boostList != null) {
            boostList.remove(boost);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSerialKill() {
        return serialKill;
    }

    public void setSerialKill(int serialKill) {
        this.serialKill = serialKill;
    }

    public long getBoostdelay() {
        return boostdelay;
    }

    public void setBoostdelay(long boostdelay) {
        this.boostdelay = boostdelay;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getFlyTime() {
        return flyTime;
    }

    public void setFlyTime(int flyTime) {
        this.flyTime = flyTime;
    }

    public boolean isFlyTimeChanged() {
        return flyTimeChanged;
    }

    public void setFlyTimeChanged(boolean flyTimeChanged) {
        this.flyTimeChanged = flyTimeChanged;
    }

    public long getLastClassificationUpdate() {
        return lastClassificationUpdate;
    }

    public void setLastClassificationUpdate(long lastClassificationUpdate) {
        this.lastClassificationUpdate = lastClassificationUpdate;
    }

    public void setBoosts(Map<BoostType, List<Boost>> boosts) {
        this.boosts = boosts;
    }

    public Map<String, Long> getCommandCooldowns() {
        return commandCooldowns;
    }

    public void setCommandCooldowns(Map<String, Long> commandCooldowns) {
        this.commandCooldowns = commandCooldowns;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
