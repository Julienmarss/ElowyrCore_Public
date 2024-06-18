package fr.elowyr.core.lang;

import fr.elowyr.core.interfaces.TypedMessage;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.entity.Player;

class ActionBarMessage implements TypedMessage
{
    private final String message;
    
    public ActionBarMessage(final String message) {
        this.message = message;
    }
    
    @Override
    public void send(final Player player, final Object... replacements) {
        BukkitUtils.sendActionBar(player, Utils.replaceAll(this.message, replacements));
    }
    
    @Override
    public void broadcast(final Object... replacements) {
        BukkitUtils.broadcastActionBar(Utils.replaceAll(this.message, replacements));
    }
    
    @Override
    public String toString() {
        return this.message;
    }
}
