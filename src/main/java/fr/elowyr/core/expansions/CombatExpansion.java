package fr.elowyr.core.expansions;

import fr.elowyr.core.ElowyrCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombatExpansion extends PlaceholderExpansion {

    private final CombatTagPlus combatTagPlus = (CombatTagPlus) ElowyrCore.getInstance().getServer().getPluginManager().getPlugin("CombatTagPlus");

    @Override
    public @NotNull String getIdentifier() {
        return "elowyr";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AnZok";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("tag")) {
            return combatTagPlus.getTagManager().getTag(player.getUniqueId()).getTagDuration() + "s";
        }
        return null;
    }
}
