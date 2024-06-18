package fr.elowyr.core.outpost.data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Captured extends OutPostStatus {
    private final String factionId;

    private Faction faction;

    private BukkitTask rewardTask;

    private final long capturedTime;

    public Captured(OutPost outPost, String currentFactionId) {
        super(outPost);
        this.factionId = currentFactionId;
        startRewards();
        this.capturedTime = System.currentTimeMillis();
    }

    public boolean isOwner(String factionId) {
        return this.factionId.equals(factionId);
    }

    public OutPostStatus onPlayerEnter(FPlayer fp) {
        if (!fp.getFactionId().equals(this.factionId)) {
            if (hasFactionOwner(this.outPost.getPlayersInArea()))
                return this;
            stopRewards();
            Faction faction = getFaction();
            Lang.broadcast("listeners.outpost.ownership.loosing", "faction", faction.getTag(), "outpost", this.outPost
                    .getName());
            return (new CapturingDown(this.outPost, 100, this.factionId)).start();
        }
        return this;
    }

    public OutPostStatus onPlayerExit(FPlayer fp) {
        Set<FPlayer> players = this.outPost.getPlayersInArea();
        if (!hasFactionOwner(players)) {
            Faction selected = getSelectFaction(players);
            if (selected != null) {
                stopRewards();
                return (new CapturingDown(this.outPost, 100, this.factionId)).start();
            }
        }
        return this;
    }

    public OutPostStatus onPlayersEnter(Set<FPlayer> players) {
        for (FPlayer fp : players) {
            if (!fp.getFactionId().equals(this.factionId))
                return onPlayerEnter(fp);
        }
        return this;
    }

    public void stop() {
        stopRewards();
    }

    public void onRewardPeriodUpdated() {
        stopRewards();
        startRewards();
    }

    public String getStatus() {
        return "captured";
    }

    public int getPercentage() {
        return 100;
    }

    public String getCurrentFactionTag() {
        return getFaction().getTag();
    }

    public String getCatchFactionTag() {
        return Lang.get().getString("scoreboard.outpost.no-faction");
    }

    public int getCaptureTime() {
        return (int)((System.currentTimeMillis() - this.capturedTime) / 1000L);
    }

    private Faction getSelectFaction(Set<FPlayer> players) {
        List<Faction> factions = players.stream().map(FPlayer::getFaction).filter(Faction::isNormal).distinct().collect(Collectors.toList());
        return (factions.size() == 1) ? factions.get(0) : null;
    }

    private boolean hasFactionOwner(Set<FPlayer> players) {
        return players.stream().anyMatch(fp -> fp.getFactionId().equals(this.factionId));
    }

    private void startRewards() {
        long period = this.outPost.getRewardPeriod();
        if (this.rewardTask == null)
            this.rewardTask = Bukkit.getScheduler().runTaskTimer(ElowyrCore.getInstance(), this::giveRewards, period, period);
    }

    private void stopRewards() {
        if (this.rewardTask != null) {
            this.rewardTask.cancel();
            this.rewardTask = null;
        }
    }

    private void giveRewards() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Faction faction = getFaction();
        if (faction == null)
            return;
        for (Player player : faction.getOnlinePlayers()) {
            for (String command : this.outPost.getRewardCommands())
                Bukkit.dispatchCommand(console, command.replace("{player}", player.getName()));
        }
    }

    private Faction getFaction() {
        if (this.faction == null)
            this.faction = Factions.getInstance().getFactionById(this.factionId);
        return this.faction;
    }
}