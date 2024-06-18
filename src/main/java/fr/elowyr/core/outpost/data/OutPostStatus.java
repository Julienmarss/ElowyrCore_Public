package fr.elowyr.core.outpost.data;

import com.massivecraft.factions.FPlayer;
import fr.elowyr.core.data.events.OutPost;

import java.util.Set;

public abstract class OutPostStatus {
    protected final OutPost outPost;

    public OutPostStatus(OutPost outPost) {
        this.outPost = outPost;
    }

    public abstract boolean isOwner(String paramString);

    public OutPostStatus onPlayerEnter(FPlayer fp) {
        return this;
    }

    public OutPostStatus onPlayersEnter(Set<FPlayer> players) {
        return this;
    }

    public OutPostStatus onPlayerExit(FPlayer fp) {
        return this;
    }

    public void onRewardPeriodUpdated() {}

    public OutPostStatus start() {
        return this;
    }

    public void stop() {}

    public abstract String getStatus();

    public abstract int getPercentage();

    public abstract String getCurrentFactionTag();

    public abstract String getCatchFactionTag();

    public int getCaptureTime() {
        return 0;
    }
}
