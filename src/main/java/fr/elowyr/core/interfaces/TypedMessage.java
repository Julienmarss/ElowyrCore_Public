package fr.elowyr.core.interfaces;

import org.bukkit.entity.Player;

public interface TypedMessage
{
    void send(final Player p0, final Object... p1);
    
    void broadcast(final Object... p0);
}
