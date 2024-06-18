package fr.elowyr.core.expansions;

import fr.elowyr.core.voteparty.VoteManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VotePartyExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "voteparty";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AnZok";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        params = params.toLowerCase();
        if (params.equals("server_goal"))
            return String.valueOf(VoteManager.get().getServerGoal());
        if (params.equals("server_current"))
            return String.valueOf(VoteManager.get().getServerCurrent());
        return null;
    }
}
