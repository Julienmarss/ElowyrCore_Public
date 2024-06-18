package fr.elowyr.core.expansions;

import com.avaje.ebean.validation.NotNull;
import fr.elowyr.core.lang.Lang;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class HearthExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "elowyrhearth";
    }

    @Override
    public String getAuthor() {
        return "AnZok";
    }

    @Override
    public  String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NotNull String onPlaceholderRequest(final Player player, final String params) {
        return player.hasPermission("elowyrcore.hearth") ? Lang.get().getString("prime") : "";
    }
}
