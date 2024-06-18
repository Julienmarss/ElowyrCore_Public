package fr.elowyr.core.data;

import com.massivecraft.factions.Faction;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class AntiCleanUpItemList {

    private final Faction faction;
    private final List<UUID> itemIds;
    private final Hologram hologram;
    private BukkitTask task;

    public AntiCleanUpItemList(final Faction faction, final List<UUID> itemIds, final Hologram hologram) {
        this.faction = faction;
        this.itemIds = itemIds;
        this.hologram = hologram;
        this.task = null;
    }

    public boolean isItem(final Item item) {
        return this.itemIds.contains(item.getUniqueId());
    }

    public void removeItem(final Item item) {
        this.itemIds.remove(item.getUniqueId());
    }

    public boolean isEmpty() {
        return this.itemIds.isEmpty();
    }

    public Faction getFaction() {
        return faction;
    }

    public List<UUID> getItemIds() {
        return itemIds;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
