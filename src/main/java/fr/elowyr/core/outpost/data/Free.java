package fr.elowyr.core.outpost.data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.lang.Lang;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Free extends OutPostStatus {
    public Free(OutPost outPost) {
        super(outPost);
    }

    public boolean isOwner(String factionId) {
        return false;
    }

    public OutPostStatus onPlayerEnter(FPlayer fp) {
        Faction f = fp.getFaction();
        Lang.broadcast("listeners.outpost.ownership.start", "faction", f.getTag(), "outpost", this.outPost.getName());
        return (new CapturingUp(this.outPost, 0, f.getId())).start();
    }

    public OutPostStatus onPlayerExit(FPlayer fp) {
        Set<FPlayer> players = this.outPost.getPlayersInArea();
        Faction selected = getSelectFaction(players);
        if (selected != null) {
            return (new CapturingUp(this.outPost, 0, selected.getId())).start();
        }
        return this;
    }

    public OutPostStatus onPlayersEnter(Set<FPlayer> players) {
        Faction selected = getSelectFaction(players);
        if (selected != null) {
            return (new CapturingUp(this.outPost, 0, selected.getId())).onPlayersEnter(players).start();
        }
        return this;
    }

    private Faction getSelectFaction(Set<FPlayer> players) {
        List<Faction> factions = players.stream().map(FPlayer::getFaction).filter(Faction::isNormal).distinct().collect(Collectors.toList());
        return (factions.size() == 1) ? factions.get(0) : null;
    }

    public String getStatus() {
        return "free";
    }

    public int getPercentage() {
        return 0;
    }

    public String getCurrentFactionTag() {
        return Lang.get().getString("scoreboard.outpost.no-faction");
    }

    public String getCatchFactionTag() {
        return Lang.get().getString("scoreboard.outpost.no-faction");
    }
}
