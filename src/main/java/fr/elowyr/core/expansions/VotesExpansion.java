package fr.elowyr.core.expansions;

import com.avaje.ebean.validation.NotNull;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.user.UserManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class VotesExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull
    String getIdentifier() {
        return "votes";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "AnZok";
    }

    @Override
    public @NotNull
    String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String params) {
        if (player == null)
            return null;
        User user = UserManager.get(player.getUniqueId());
        return "" + user.getVotes();
    }
}
