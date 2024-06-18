package fr.elowyr.core.ap;

import com.massivecraft.factions.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.elowyr.core.ap.data.AP;
import fr.elowyr.core.ap.guis.ApBuyConfirmGUI;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.managers.DataManager;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class APManager {
    private static APManager instance;
    private final HashMap<String, AP> aps = new HashMap<>();
    private final transient WorldGuardPlugin wg;

    public APManager() {
        this.wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    }

    public void load() throws Throwable {
        final ResultSet rs = DataManager.get().prepareStatement("SELECT * FROM `{aps}`").executeQuery();
        while (rs.next()) {
            final AP ap = DbUtils.loadAp(rs);
            aps.put(ap.getChunkAsString(), ap);
        }
    }

    public void addAp(Player player, long price, boolean isVIP) {

        final Chunk chunk = player.getLocation().getChunk();
        final String chunkAsString = this.changeChunkToString(chunk);

        if (aps.containsKey(chunkAsString)) {
            Lang.send(player, "ap.already_exist");
            return;
        }

        if (isVIP && aps.values().stream().anyMatch(AP::isVIP)) {
            Lang.send(player, "ap.already_vip");
            return;
        }

        Lang.send(player, "ap.create", "price", price);
        AP ap = new AP(chunk.getX(), chunk.getZ(), chunk.getWorld().getName(), price, isVIP, chunkAsString);
        aps.put(chunkAsString, ap);
        DbUtils.insertAp(ap);
    }

    /**
     * @param player the player who want to delete the ap
     */
    public void deleteAp(Player player) {
        final Chunk chunk = player.getLocation().getChunk();
        final String chunkAsString = this.changeChunkToString(chunk);

        if (!aps.containsKey(chunkAsString)) {
            Lang.send(player, "ap.doesnt_exist");
            return;
        }

        final AP ap = aps.get(chunkAsString);

        aps.remove(chunkAsString);
        DbUtils.deleteAp(ap);
        Lang.send(player, "ap.delete");
    }

    public void resetAP(Player player) {
        final Chunk chunk = player.getLocation().getChunk();
        final String chunkAsString = this.changeChunkToString(chunk);

        final Optional<AP> optional = Optional.ofNullable(aps.get(chunkAsString));
        if (!optional.isPresent()) {
            Lang.send(player, "ap.doesnt_exist");
            return;
        }

        final AP ap = optional.get();
        ap.reset();
        Board.getInstance().setFactionAt(Factions.getInstance().getWilderness(), new FLocation(chunk));

        Lang.send(player, "ap.reset");
    }

    /**
     * @param player the player who want to have informations about ap
     */
    public void sendInformation(Player player) {
        final Chunk chunk = player.getLocation().getChunk();
        final String chunkAsString = this.changeChunkToString(chunk);

        final Optional<AP> optional = Optional.ofNullable(aps.get(chunkAsString));
        if (!optional.isPresent()) {
            Lang.send(player, "ap.doesnt_exist");
            return;
        }

        final AP ap = optional.get();

        Lang.send(player, "ap.info", "x", ap.getX(), "z", ap.getZ(), "world", ap.getWorld(), "price", ap.getPrice(),
                "faction", ap.isBuyable() ? "§cAucune faction" : Factions.getInstance().getFactionById(ap.getCurrentOwnerId()).getTag(),
                "date", ap.isBuyable() ? "§cX" : this.getStartedDate(String.valueOf(ap.getBuyAt())));
    }

    /**
     * @param player the player who want to buy the ap
     */

    public void buyApCheck(Player player) {
        final Chunk chunk = player.getLocation().getChunk();
        final String chunkAsString = this.changeChunkToString(chunk);

        final Optional<AP> optional = Optional.ofNullable(aps.get(chunkAsString));
        if (!optional.isPresent()) {
            Lang.send(player, "ap.doesnt_exist");
            return;
        }

        final AP ap = optional.get();
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        if (ap.isOwner(fPlayer.getFaction().getId()))
            Lang.send(player, "ap.buy.error-owner");
        else if (!ap.isBuyable())
            Lang.send(player, "ap.buy.error");
        else if (!VaultUtils.has(player, ap.getPrice()))
            Lang.send(player, "ap.buy.error-price");
        else if (fPlayer.getFaction().isWilderness())
            Lang.send(player, "ap.buy.error-faction");
        else if (count(fPlayer.getFaction().getId()) >= Config.get().getMaxAp())
            Lang.send(player, "ap.buy.error-limit");
        else {
            final ApBuyConfirmGUI gui = new ApBuyConfirmGUI(this, ap.getPrice());
            gui.open(player);
        }
    }

    public void buyAp(Player player) {
        final Chunk chunk = player.getLocation().getChunk();
        final String chunkAsString = this.changeChunkToString(chunk);

        final Optional<AP> optional = Optional.ofNullable(aps.get(chunkAsString));
        if (!optional.isPresent()) {
            Lang.send(player, "ap.doesnt_exist");
            return;
        }

        final AP ap = optional.get();
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        if (ap.isOwner(fPlayer.getFaction().getId()))
            Lang.send(player, "ap.buy.error-owner");
        else if (!ap.isBuyable())
            Lang.send(player, "ap.buy.error");
        else if (!VaultUtils.has(player, ap.getPrice()))
            Lang.send(player, "ap.buy.error-price");
        else if (fPlayer.getFaction().isWilderness())
            Lang.send(player, "ap.buy.error-faction");
        else if (count(fPlayer.getFaction().getId()) >= Config.get().getMaxAp())
            Lang.send(player, "ap.buy.error-limit");
        else {
            VaultUtils.withdrawMoney(player, ap.getPrice());
            Lang.send(player, "ap.buy.success");
            ap.setCurrentOwnerId(fPlayer.getFactionId());
            Board.getInstance().setFactionAt(fPlayer.getFaction(), new FLocation(chunk));
        }
    }

    public long count(String id) {
        return aps.values().stream().filter(e -> e.isOwner(id)).count();
    }

    public void removeAllClaims(String fId) {
        for (Map.Entry<String, AP> entry : aps.entrySet()) {
            if (entry.getValue().isOwner(fId))
                entry.getValue().removeClaims();
        }
    }

    public void removeClaims(FLocation fLocation) {
        for (Map.Entry<String, AP> entry : aps.entrySet()) {
            if (entry.getValue().isLocation(fLocation))
                entry.getValue().removeClaims();
        }
    }

    public Optional<AP> getVIP() {
        return aps.values().stream().filter(AP::isVIP).findAny();
    }

    /**
     * @param chunk the chunk to convert
     * @return chunk as string
     */
    public String changeChunkToString(Chunk chunk) {
        String c = chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
        return c;
    }

    public Long getStartedDate(String startedDate) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(startedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (date != null ? date.getTime() : 0) - 1000;
    }

    public static void set(final APManager instance) {
        APManager.instance = instance;
    }

    public static APManager get() {
        return APManager.instance;
    }

    public static APManager getInstance() {
        return instance;
    }

    public static void setInstance(APManager instance) {
        APManager.instance = instance;
    }

    public HashMap<String, AP> getAps() {
        return aps;
    }

    public WorldGuardPlugin getWg() {
        return wg;
    }
}
