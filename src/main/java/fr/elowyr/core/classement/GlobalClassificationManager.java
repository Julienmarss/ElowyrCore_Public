package fr.elowyr.core.classement;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import fr.elowyr.core.classement.data.*;
import fr.elowyr.core.utils.DbUtils;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class GlobalClassificationManager {

    private static GlobalClassificationManager instance;
    private final ClassificationManager<String> factions;
    private final ClassificationManager<UUID> users;

    public GlobalClassificationManager() {
        this.factions = new ClassificationManager<>("faction_classification", FactionKey.values());
        this.users = new ClassificationManager<>("user_classification", UserKey.values());
    }
    
    public void load() {
        DbUtils.loadClassification(this.factions, Function.identity());
        DbUtils.loadClassification(this.users, UUID::fromString);
        for (final UserKey key : UserKey.values()) {
            this.users.update(key);
        }
        for (final FactionKey key2 : FactionKey.values()) {
            this.factions.update(key2);
        }
    }
    
    public void addFactionValue(final Faction f, final FactionKey key, final double value) {
        this.factions.addValue(f.getId(), f.getTag(), key, value);
    }
    
    public void addUserValue(final UUID userId, final String name, final UserKey key, final double value) {
        this.users.addValue(userId, name, key, value);
    }
    
    public void addUserValueNoUpdate(final UUID userId, final String name, final UserKey key, final double value) {
        this.users.addValueNoUpdate(userId, name, key, value);
    }
    
    public void updateUsers(final FieldKey key) {
        this.users.update(key);
    }
    
    public int saveSync(final byte type) {
        final AtomicInteger accumulator = new AtomicInteger(0);
        if ((type & 0x1) != 0x0) {
            DbUtils.updateClassification(this.factions, accumulator::addAndGet);
        }
        if ((type & 0x2) != 0x0) {
            DbUtils.updateClassification(this.users, accumulator::addAndGet);
        }
        return accumulator.get();
    }
    
    public void setFactionTag(final String id, final String display) {
        final ClassificationParent<String> parent = this.factions.getByUuid(id);
        if (parent != null) {
            parent.setDisplay(display);
            DbUtils.updateClassificationDisplay(this.factions, parent);
        }
    }
    
    public void setUsername(final UUID id, final String name) {
        final ClassificationParent<UUID> parent = this.users.getByUuid(id);
        if (parent != null) {
            parent.setDisplay(name);
            DbUtils.updateClassificationDisplay(this.users, parent);
        }
    }
    
    public void removeFaction(final String id) {
        final ClassificationParent<String> parent = this.factions.getByUuid(id);
        if (parent != null) {
            this.factions.remove(parent);
            DbUtils.deleteClassification(this.factions, parent);
        }
    }
    
    public void resetUsers(final FieldKey key) {
        this.users.reset(key);
        DbUtils.resetClassification(this.users, key);
    }
    
    public void resetFactions(final FieldKey key) {
        this.factions.reset(key);
        DbUtils.resetClassification(this.factions, key);
    }
    
    public void resetAll() {
        this.resetFactions(FactionKey.PVP);
        this.resetFactions(FactionKey.FARM);
        this.resetUsers(UserKey.JOBS_POINTS);
    }
    
    public ClassificationParent<?> parse(final byte type, final FieldKey key, final int id) {
        final ClassificationManager<?> manager = ((type == 1) ? this.factions : this.users);
        return manager.getAt(key, id);
    }
    
    public ClassificationParent<?> getForPlayer(final Player player, final byte type, final FieldKey key) {
        if (type == 1) {
            final FPlayer fp = FPlayers.getInstance().getByPlayer(player);
            return this.factions.getByUuid(fp.getFactionId());
        }
        return this.users.getByUuid(player.getUniqueId());
    }
    
    public static void set(final GlobalClassificationManager instance) {
        GlobalClassificationManager.instance = instance;
    }
    
    public static GlobalClassificationManager get() {
        return GlobalClassificationManager.instance;
    }
}
