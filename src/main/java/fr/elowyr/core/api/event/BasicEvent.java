package fr.elowyr.core.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class BasicEvent extends Event
{
    public boolean run() {
        Bukkit.getPluginManager().callEvent((Event)this);
        return false;
    }
}
