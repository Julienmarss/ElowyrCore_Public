package fr.elowyr.core.expansions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.elowyr.core.data.events.OutPost;
import fr.elowyr.core.outpost.data.OutPostStatus;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.outpost.OutpostsManager;
import fr.elowyr.core.utils.TimeUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class OutPostExpansion extends PlaceholderExpansion {
    private final ThreadLocal<Matcher> MATCHER = ThreadLocal.withInitial(() -> Pattern.compile("^(\\w+)_(time|faction|percentage|status|catch)$").matcher(""));

    public String getIdentifier() {
        return "outpost";
    }

    public String getAuthor() {
        return "reluije";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String params) {
        Matcher matcher = this.MATCHER.get();
        if (matcher.reset(params).matches()) {
            String name = matcher.group(1);
            String type = matcher.group(2);
            OutPost outPost = OutpostsManager.get().get(name);
            if (outPost == null)
                return null;
            OutPostStatus status = outPost.getStatus();
            switch (type) {
                case "time":
                    return TimeUtils.format(status.getCaptureTime());
                case "faction":
                    return status.getCurrentFactionTag();
                case "catch":
                    return status.getCatchFactionTag();
                case "percentage":
                    return String.valueOf(status.getPercentage());
                case "status":
                    return Lang.get().getString("scoreboard.outpost.status." + status.getStatus());
            }
        } else if (params.equals("status")) {
            if (OutpostsManager.get().isEnabled())
                return Lang.get().getString("scoreboard.outpost.enabled");
            return Lang.get().getString("scoreboard.outpost.disabled");
        }
        return null;
    }
}