package fr.elowyr.core.commands.faction;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL; 
import com.massivecraft.factions.util.WarmUpUtil;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.user.data.User;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.user.UserManager;
import fr.elowyr.core.utils.CooldownUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FFlyCommand extends CmdFly {

    public FFlyCommand() {
        this.aliases.add("fly");
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (commandContext.args.size() == 0) {
            toggleFlight(commandContext, !commandContext.fPlayer.isFlying(), true);
        } else if (commandContext.args.size() == 1) {
            if (commandContext.argAsString(0).equalsIgnoreCase("auto")) {
                if (Permission.FLY_AUTO.has(commandContext.player, true)) {
                    commandContext.fPlayer.setAutoFlying(!commandContext.fPlayer.isAutoFlying());
                    toggleFlight(commandContext, commandContext.fPlayer.isAutoFlying(), false);
                }
            } else {
                toggleFlight(commandContext, commandContext.argAsBool(0), true);
            }
        }
    }

    private void toggleFlight(final CommandContext var1, final boolean var2, final boolean var3) {
        if (CooldownUtils.isOnCooldown("fly", var1.player)) {
            var1.player.sendMessage(Utils.color("&cVeuillez patienter avant de refaire cette commande."));
            return;
        }
        if (!var2) {
            var1.fPlayer.setFlying(false);
        } else {
            final User user = UserManager.get(var1.player.getUniqueId());
            if (this.flyTest(var1, user, var3)) {
                var1.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", () -> {
                    if (this.flyTest(var1, user, var3) && user.canUseCommand("ffly", var3)) {
                        var1.fPlayer.setFlying(true);
                    }
                }, FactionsPlugin.getInstance().conf().commands().fly().getDelay());
                CooldownUtils.addCooldown("fly", var1.player, 15);
            }
        }
    }

    private boolean flyTest(CommandContext var1, User user, boolean var2) {
        if (user.getFlyTime() <= 0 && !var1.player.hasPermission("elowyrcore.fly.infini")) {
            if (var2) {

                Player player = (Player) var1.sender;

                if (this.hasFullArmor(player.getInventory()))
                    return true;

                Lang.send(var1.sender, "flytime.no-time");
            }

            return false;
        }
        if (!var1.fPlayer.canFlyAtLocation()) {
            if (var2) {
                Faction var3 = Board.getInstance().getFactionAt(var1.fPlayer.getLastStoodAt());
                var1.msg(TL.COMMAND_FLY_NO_ACCESS, var3.getTag(var1.fPlayer));
            }
            return false;
        }
        if (ElowyrCore.getInstance().nearEnemy(var1.player.getLocation(), var1.fPlayer.getFaction())) {
            if (var2)
                var1.msg(TL.COMMAND_FLY_ENEMY_NEARBY);
            return false;
        }
        return true;
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
