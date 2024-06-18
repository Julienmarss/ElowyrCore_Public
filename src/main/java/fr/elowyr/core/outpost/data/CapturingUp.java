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

import java.util.Set;

public class CapturingUp extends OutPostStatus {
    private int progression;

    private final String catcherFactionId;

    private Faction catcherFaction;

    private BukkitTask captureTask;

    private int catcherAmount;

    private int blockerAmount;

    public CapturingUp(OutPost outPost, int progression, String catcherFactionId) {
        super(outPost);
        this.progression = progression;
        this.catcherFactionId = catcherFactionId;
        this.catcherAmount = 0;
        this.blockerAmount = 0;
    }

    public boolean isOwner(String factionId) {
        return false;
    }

    public OutPostStatus onPlayerEnter(FPlayer fp) {
        if (fp.getFactionId().equals(this.catcherFactionId)) {
            this.catcherAmount++;
        } else {
            this.blockerAmount++;
        }
        return this;
    }

    public OutPostStatus onPlayerExit(FPlayer fp) {
        if (fp.getFactionId().equals(this.catcherFactionId)) {
            this.catcherAmount--;
        } else {
            this.blockerAmount--;
        }
        return this;
    }

    public OutPostStatus onPlayersEnter(Set<FPlayer> players) {
        for (FPlayer fp : players)
            onPlayerEnter(fp);
        return this;
    }

    public CapturingUp start() {
        if (this.captureTask == null)
            this.captureTask = Bukkit.getScheduler().runTaskTimer(ElowyrCore.getInstance(), this::update, 0L, OutpostsManager.get().getCaptureTaskPeriod());
        return this;
    }

    public void stop() {
        if (this.captureTask != null) {
            this.captureTask.cancel();
            this.captureTask = null;
        }
    }

    public String getStatus() {
        return "capturing-up";
    }

    public int getPercentage() {
        return this.progression;
    }

    public String getCurrentFactionTag() {
        return getFactionPlaceHolder(null);
    }

    public String getCatchFactionTag() {
        return getFactionPlaceHolder(getCatcherFaction());
    }

    private void update() {
        if (this.catcherAmount > 0 && this.blockerAmount == 0) {
            this.progression++;
            if (OutpostsManager.get().getBroadcastPercentages().contains(this.progression))
                Lang.broadcast("listeners.outpost.ownership.capturing", "faction", getCatchFactionTag(), "outpost", this.outPost
                        .getName(), "percentage", this.progression);
            if (this.progression >= 100) {
                stop();
                this.outPost.setStatus(new Captured(this.outPost, this.catcherFactionId));
                Lang.broadcast("listeners.outpost.ownership.captured", "faction", getCatchFactionTag(), "outpost", this.outPost
                        .getName());
            }
        } else if (this.catcherAmount == 0 && this.blockerAmount > 0) {
            this.progression--;
            if (OutpostsManager.get().getBroadcastPercentages().contains(this.progression))
                Lang.broadcast("listeners.outpost.ownership.decapturing", "faction", getCatchFactionTag(), "outpost", this.outPost
                        .getName(), "percentage", this.progression);
            if (this.progression <= 0) {
                stop();
                this.outPost.setStatus(new Free(this.outPost));
                Lang.broadcast("listeners.outpost.ownership.lost", "faction", getCatchFactionTag(), "outpost", this.outPost
                        .getName());
            }
        }
    }

    private String getFactionPlaceHolder(Faction faction) {
        return (faction != null) ? faction.getTag() : Lang.get().getString("scoreboard.outpost.no-faction");
    }

    private Faction getCatcherFaction() {
        if (this.catcherFaction == null)
            this.catcherFaction = Factions.getInstance().getFactionById(this.catcherFactionId);
        return this.catcherFaction;
    }

    public String toString() {
        return "CapturingUp(progression=" + this.progression + ", currentFactionId='" + this.catcherFactionId + ", catcherAmount=" + this.catcherAmount + ", blockerAmount=" + this.blockerAmount + ")";
    }
}