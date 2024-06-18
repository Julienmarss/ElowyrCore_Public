package fr.elowyr.core.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.enums.StopReason;
import fr.elowyr.core.events.EventStartEvent;
import fr.elowyr.core.events.EventStopEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Set;

public abstract class IEvent {

    private String name;
    private Set<Listener> listeners;
    private int timer, defaultTime;
    private boolean started;
    private List<String> rewards;

    public IEvent(String name) {
        this.name = name;
        this.listeners = Sets.newConcurrentHashSet();
        this.started = false;
        this.rewards = Lists.newArrayList();
    }

    public void run(final String name) {
        listeners.forEach(listener -> ElowyrCore.getInstance().getServer().getPluginManager().registerEvents(listener, ElowyrCore.getInstance()));
        this.setStarted(true);
        this.start(name);
        ElowyrCore.getInstance().getServer().getPluginManager().callEvent(new EventStartEvent(this));
    }

    public void stopEvent(StopReason stopReason) {
        this.listeners.forEach(HandlerList::unregisterAll);
        this.setStarted(false);
        this.stop(stopReason);
        ElowyrCore.getInstance().getServer().getPluginManager().callEvent(new EventStopEvent(this));
    }

    abstract public void start(final String name);
    abstract public void stop(StopReason stopReason);
    public abstract String[] getLines();

    public void registerListener(Listener listener) {
        this.listeners.add(listener);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Listener> getListeners() {
        return listeners;
    }

    public void setListeners(Set<Listener> listeners) {
        this.listeners = listeners;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(int defaultTime) {
        this.defaultTime = defaultTime;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }
}
