package fr.elowyr.core.outpost.data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.Set;

public class CapturingDown extends OutPostStatus {
    private int progression;

    private final String currentFactionId;

    private Faction currentFaction;

    private BukkitTask captureTask;

    private int ownerAmount;

    private int catcherAmount;

    public CapturingDown(OutPost outPost, int progression, String currentFactionId) {
        super(outPost);
        this.progression = progression;
        this.currentFactionId = currentFactionId;
        this.ownerAmount = 0;
        this.catcherAmount = 0;
    }

    public boolean isOwner(String factionId) {
        return Objects.equals(this.currentFactionId, factionId);
    }

    public OutPostStatus onPlayerEnter(FPlayer fp) {
        if (fp.getFactionId().equals(this.currentFactionId)) {
            this.ownerAmount++;
        } else {
            this.catcherAmount++;
        }
        return this;
    }

    public OutPostStatus onPlayerExit(FPlayer fp) {
        if (fp.getFactionId().equals(this.currentFactionId)) {
            this.ownerAmount--;
        } else {
            this.catcherAmount--;
        }
        return this;
    }

    public OutPostStatus onPlayersEnter(Set<FPlayer> players) {
        for (FPlayer fp : players)
            onPlayerEnter(fp);
        return this;
    }

    public CapturingDown start() {
        if (this.captureTask == null)
            this
                    .captureTask = Bukkit.getScheduler().runTaskTimer(ElowyrCore.getInstance(), this::update, 0L, OutpostsManager.get().getCaptureTaskPeriod());
        return this;
    }

    public void stop() {
        if (this.captureTask != null) {
            this.captureTask.cancel();
            this.captureTask = null;
        }
    }

    public String getStatus() {
        return "capturing-down";
    }

    public int getPercentage() {
        return this.progression;
    }

    public String getCurrentFactionTag() {
        return getFactionPlaceHolder(getCurrentFaction());
    }

    public String getCatchFactionTag() {
        return getFactionPlaceHolder(null);
    }

    private void update() {
        if (this.catcherAmount > 0 && this.ownerAmount == 0) {
            this.progression--;
            if (OutpostsManager.get().getBroadcastPercentages().contains(this.progression))
                Lang.broadcast("listeners.outpost.ownership.decapturing", "faction", getCurrentFactionTag(), "outpost", this.outPost
                        .getName(), "percentage", this.progression);
            if (this.progression <= 0) {
                stop();
                this.outPost.setStatus(new Free(this.outPost));
                Lang.broadcast("listeners.outpost.ownership.lost", "faction", getCurrentFactionTag(), "outpost", this.outPost
                        .getName());
            }
        } else if (this.catcherAmount == 0 && this.ownerAmount > 0) {
            this.progression++;
            if (OutpostsManager.get().getBroadcastPercentages().contains(this.progression))
                Lang.broadcast("listeners.outpost.ownership.capturing", "faction", getCurrentFactionTag(), "outpost", this.outPost
                        .getName(), "percentage", this.progression);
            if (this.progression >= 100) {
                stop();
                this.outPost.setStatus(new Captured(this.outPost, this.currentFactionId));
                Lang.broadcast("listeners.outpost.ownership.captured", "faction", getCurrentFactionTag(), "outpost", this.outPost
                        .getName());
            }
        }
    }

    private String getFactionPlaceHolder(Faction faction) {
        return (faction != null) ? faction.getTag() : Lang.get().getString("scoreboard.outpost.no-faction");
    }

    private Faction getCurrentFaction() {
        if (this.currentFaction == null && this.currentFactionId != null)
            this.currentFaction = Factions.getInstance().getFactionById(this.currentFactionId);
        return this.currentFaction;
    }
}