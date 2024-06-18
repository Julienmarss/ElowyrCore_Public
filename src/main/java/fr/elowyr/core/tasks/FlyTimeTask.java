package fr.elowyr.core.tasks;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import fr.elowyr.basics.factions.FactionManager;
import fr.elowyr.basics.factions.data.FactionData;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.utils.DbUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class FlyTimeTask extends BukkitRunnable {
    public void run() {
        for (final User user : UserManager.getOnlineUsers()) {
            final Player p = user.getPlayer();
            if (p != null) {
                Faction faction = FPlayers.getInstance().getByPlayer(p).getFaction();
                Location location = p.getLocation();
                FLocation fLocation = new FLocation(location);
                FactionData factionData = FactionManager.getInstance().getProvider().get(faction.getId());
                if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR) || p.hasPermission("elowyrcore.fly.infini")) {
                    continue;
                }
                if (!p.isFlying()) {
                    continue;
                }
                if (!FPlayers.getInstance().getByPlayer(p).canFlyAtLocation()) {
                    Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> FPlayers.getInstance().getByPlayer(p).setFlying(false));
                    return;
                }
                final boolean full = this.hasFullArmor(p.getInventory());
                final boolean oldFull = Manager.FARM_ARMOR_EQUIPPED.contains(p.getUniqueId());
                if (oldFull && full || factionData.getUpgradeLevel() == 8 || p.hasPermission("elowyrcore.infini-fly")) {
                    continue;
                }

                if (ElowyrCore.getInstance().nearEnemy(location, faction) || !ElowyrCore.getInstance().isIn(p, Board.getInstance().getFactionAt(fLocation))) {
                    p.sendMessage(Utils.color("&6&lFly &7◆ &fVous avez &cperdu &fvotre &efly&f. Un &4ennemi&f est à &aproximité&f !"));
                    Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> FPlayers.getInstance().getByPlayer(p).setFlying(false));
                    return;
                }

                user.addFlyTime(-1);
                final int flyTime = user.getFlyTime();
                if (flyTime > 0) {
                    continue;
                }
                Bukkit.getScheduler().runTask(ElowyrCore.getInstance(), () -> {
                    FPlayers.getInstance().getByPlayer(p).setFlying(false);
                });
                user.setFlyTimeChanged(false);
                Lang.send(p, "flytime.flying.finished");
                DbUtils.updateUser(user, "fly_time", 0);
            }
        }
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
