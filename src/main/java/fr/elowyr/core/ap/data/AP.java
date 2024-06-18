package fr.elowyr.core.ap.data;

import com.massivecraft.factions.FLocation;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.DbUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AP {

    private Long id;
    private final int x;
    private final int z;
    private final String world;
    private long price;

    private boolean isVIP;

    private long buyAt;
    private String currentOwnerId;

    private String chunkAsString;

    private Map<Material, Integer> blocks = new HashMap<>();

    public AP(int x, int z, String world, long price, boolean isVIP, String chunkAsString) {
        this(null, x, z, world, price, isVIP, null, 0, chunkAsString);
    }

    public AP(Long id, int x, int z, String world, long price, boolean isVIP, String currentOwnerId, long buyAt, String chunkAsString) {
        this.id = id;
        this.x = x;
        this.z = z;
        this.world = world;
        this.price = price;
        this.isVIP = isVIP;
        this.currentOwnerId = currentOwnerId;
        this.buyAt = buyAt;
        this.chunkAsString = chunkAsString;
    }

    /**
     * @param currentOwnerId
     *            the currentOwnerId to set
     */
    public void setCurrentOwnerId(String currentOwnerId) {
        this.currentOwnerId = currentOwnerId;
        this.buyAt = System.currentTimeMillis();
        DbUtils.updateAp(this, "owner-id", this.currentOwnerId, "date", this.buyAt);
    }

    public Map<Material, Integer> getBlocks() {
        return blocks == null ? blocks = new HashMap<>() : blocks;
    }

    public boolean addBlocks(Material material, Player player) {
        if (Config.get().getBlocks().containsKey(material)) {
            int current = getBlocks().getOrDefault(material, 0) + 1;
            if (current > Config.get().getBlocks().get(material)) {
                Lang.send(player, "ap.limited-blocks");
                return true;
            }
            getBlocks().put(material, current);
        }
        return false;
    }

    public void removeBlocks(Material material) {
        if (Config.get().getBlocks().containsKey(material) && getBlocks().containsKey(material)) {
            int current = getBlocks().getOrDefault(material, 0) - 1;
            if (current > 0)
                getBlocks().put(material, current);
            else
                getBlocks().remove(material);
        }
    }

    public boolean isBuyable() {
        return buyAt == 0;
    }

    public boolean isOwner(String factionId) {
        return currentOwnerId != null && currentOwnerId.equals(factionId);
    }

    public void reset() {
        this.buyAt = 0;
        this.currentOwnerId = null;
    }

    public void removeClaims() {
        this.buyAt = 0;
        this.currentOwnerId = null;
        this.price = 0;
    }

    public boolean isLocation(FLocation fLocation) {
        FLocation fLocation2 = new FLocation(world, x, z);
        return fLocation.equals(fLocation2);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public long getBuyAt() {
        return buyAt;
    }

    public void setBuyAt(long buyAt) {
        this.buyAt = buyAt;
    }

    public String getCurrentOwnerId() {
        return currentOwnerId;
    }

    public String getChunkAsString() {
        return chunkAsString;
    }

    public void setChunkAsString(String chunkAsString) {
        this.chunkAsString = chunkAsString;
    }

    public void setBlocks(Map<Material, Integer> blocks) {
        this.blocks = blocks;
    }
}
