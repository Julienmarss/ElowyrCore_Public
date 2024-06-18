package fr.elowyr.core.lang;

import fr.elowyr.core.interfaces.TypedMessage;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.entity.Player;

public class BigTitleMessage implements TypedMessage {

    private final String title;
    private final String subTitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public BigTitleMessage(final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }
    
    @Override
    public void send(final Player player, final Object... replacements) {
        BukkitUtils.sendTitle(player, Utils.replaceAll(this.title, replacements), Utils.replaceAll(this.subTitle, replacements), this.fadeIn, this.stay, this.fadeOut);
    }
    
    @Override
    public void broadcast(final Object... replacements) {
        BukkitUtils.broadcastTitle(Utils.replaceAll(this.title, replacements), Utils.replaceAll(this.subTitle, replacements), this.fadeIn, this.stay, this.fadeOut);
    }
    
    @Override
    public String toString() {
        return this.title + "\n" + this.subTitle;
    }
}
