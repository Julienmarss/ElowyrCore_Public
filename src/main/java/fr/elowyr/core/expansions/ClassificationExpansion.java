package fr.elowyr.core.expansions;

import com.avaje.ebean.validation.NotNull;
import fr.elowyr.core.classement.GlobalClassificationManager;
import fr.elowyr.core.classement.data.ClassificationParent;
import fr.elowyr.core.classement.data.FactionKey;
import fr.elowyr.core.classement.data.FieldKey;
import fr.elowyr.core.classement.data.UserKey;
import fr.elowyr.core.lang.Lang;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassificationExpansion extends PlaceholderExpansion {

    private final ThreadLocal<Matcher> MATCHER;

    public ClassificationExpansion() {
        this.MATCHER = ThreadLocal.withInitial(() -> Pattern.compile("^(faction|player)_(key|value|index)_(\\w+)_(\\d+|mine)$").matcher(""));
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "classification";
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

    @Override
    public @NotNull
    String onPlaceholderRequest(final Player player, final String params) {
        final Matcher matcher = this.MATCHER.get();
        final GlobalClassificationManager gcm = GlobalClassificationManager.get();
        if (!matcher.reset(params).matches()) {
            return null;
        }
        final byte type = (byte) (matcher.group(1).equals("faction") ? 1 : 2);
        final String data = matcher.group(2);
        final String keyName = matcher.group(3);
        final FieldKey key = (type == 1) ? FactionKey.fromName(keyName) : UserKey.fromName(keyName);
        if (key == null) {
            return null;
        }
        ClassificationParent<?> parent;
        if (matcher.group(4).equals("mine")) {
            parent = gcm.getForPlayer(player, type, key);
        } else {
            final int id = Integer.parseInt(matcher.group(4));
            parent = gcm.parse(type, key, id);
        }
        if (data.equals("key")) {
            if (parent == null) {
                return Lang.get().getString("scoreboard.classification.no-key");
            }
            return parent.getDisplay();
        } else if (data.equals("index")) {
            if (parent == null) {
                return Lang.get().getString("scoreboard.classification.no-index");
            }
            return String.valueOf(parent.getIndex(key) + 1);
        } else {
            if (parent == null) {
                return Lang.get().getString("scoreboard.classification.no-value");
            }
            return key.format(parent.get(key));
        }
    }
}
