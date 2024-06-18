package fr.elowyr.core.expansions;

import com.avaje.ebean.validation.NotNull;
import com.massivecraft.factions.FPlayers;
import fr.elowyr.basics.factions.FactionManager;
import fr.elowyr.basics.factions.data.FactionData;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.TimeUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FlyTimeExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull
    String getIdentifier() {
        return "flytime";
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

    public String onPlaceholderRequest(Player player, String params) {
        if (player == null)
            return null;
        FactionData factionData = FactionManager.getInstance().getProvider().get(FPlayers.getInstance().getByPlayer(player).getFactionId());
        final boolean full = this.hasFullArmor(player.getInventory());
        final boolean oldFull = Manager.FARM_ARMOR_EQUIPPED.contains(player.getUniqueId());
        if (oldFull && !full || player.hasPermission("elowyrcore.fly.infini") || factionData != null && factionData.getUpgradeLevel() == 8) {
            return Lang.get().getString("flytime.infini");
        }
        User user = UserManager.get(player.getUniqueId());
        int flytime = (user != null) ? user.getFlyTime() : 0;
        if (flytime <= 0)
            return Lang.get().getString("flytime.none");
        return TimeUtils.format(flytime);
    }

    private boolean hasFullArmor(final PlayerInventory inv) {
        for (final ItemStack armor : inv.getArmorContents()) {
            if (armor == null || armor.getType().equals(Material.AIR)) {
                return false;
            }
            final UsableItem item = ItemsManager.getInstance().getByItemStack(NMS.getNBT(armor));
            if (item != ItemsManager.getInstance().getFARM_HELMET() && item != ItemsManager.getInstance().getFARM_CHESTPLATE() && item != ItemsManager.getInstance().getFARM_LEGGINGS() && item != ItemsManager.getInstance().getFARM_BOOTS()) {
                return false;
            }
        }
        return true;
    }
}
