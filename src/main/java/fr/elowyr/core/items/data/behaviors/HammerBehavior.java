package fr.elowyr.core.items.data.behaviors;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import fr.elowyr.basics.factions.FactionManager;
import fr.elowyr.basics.factions.data.FactionData;
import fr.elowyr.basics.missions.MissionManager;
import fr.elowyr.basics.missions.data.MissionType;
import fr.elowyr.basics.upgrade.UpgradeManager;
import fr.elowyr.basics.upgrade.data.Upgrade;
import fr.elowyr.basics.upgrade.data.UpgradeType;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.utils.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HammerBehavior extends BasicBehavior {

    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private List<Material> blacklist;
    private double breakingChance;
    private int maxUses;

    public HammerBehavior() {
        super(UseType.BLOCK_BREAK);
    }

    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.maxUses = section.getInt("max-uses");
        this.blacklist = section.getStringList("blacklist").stream().map(BukkitUtils::getType).collect(Collectors.toList());
        this.breakingChance = section.getDouble("breaking-chance", 1.0);
    }

    @Override
    public void getDefaultData(final NBTData data) {
        data.setInt("block_broken", 0);
        data.setInt("uses", 0);
    }

    @Override
    public boolean use(final ItemContext<?> ctx) {
        if (ctx.getUseType().equals(UseType.BLOCK_BREAK)) {
            this.blockBreak(ctx);
        }
        return false;
    }

    private void blockBreak(final ItemContext<?> ctx) {
        final BlockBreakEvent event = (BlockBreakEvent) ctx.getEvent();
        final Player player = ctx.getPlayer();
        final FPlayer fp = FPlayers.getInstance().getByPlayer(player);
        final Block block = event.getBlock();
        FactionData factionData = FactionManager.getInstance().getProvider().get(FPlayers.getInstance().getByPlayer(player).getFactionId());
        int count = ctx.getData().getUnsignedInt("block_broken");
        event.setCancelled(true);
        if (ctx.getData().addInt("uses", 1) >= this.maxUses) {
            ctx.useItem(1);
        }
        for (int x = -1; x <= Math.abs(-1); x++) {
            for (int y = -1; y <= Math.abs(-1); y++) {
                for (int z = -1; z <= Math.abs(-1); z++) {
                    Location add = event.getBlock().getLocation().clone().add(x, y, z);
                    Block radiusBlock = add.getBlock();
                    if (radiusBlock == block) continue;
                    if (this.blacklist.contains(radiusBlock.getType())) {
                        break;
                    }
                    if (BukkitUtils.canBreak(radiusBlock, player, fp)) {
                        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(radiusBlock, ActionType.BREAK), radiusBlock);
                        radiusBlock.breakNaturally();
                        ++count;
                    }

                    List<Upgrade> upgrades = UpgradeManager.getInstance().getUpgradeByTarget(factionData, block.getType().name());
                    if (upgrades == null) return;

                    upgrades.forEach(upgrade -> {
                        if (upgrade == null) return;

                        if (upgrade.getUpgradeType() != UpgradeType.BREAK) return;
                        if (upgrade.getActualGoal() >= upgrade.getGoal()) return;
                        upgrade.setActualGoal(upgrade.getActualGoal() + 1);
                    });
                }
            }
            ctx.getData().setInt("max-uses", ctx.getData().getInt("max-uses") - 1);
        }
        if (count > 0) {
            Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(block, ActionType.BREAK), block);
            MissionManager.getInstance().updateMission(MissionType.BREAK_BLOCK, player, block.getType(), count, true);
            //PrestigesManager.getInstance().updateObjectiveProgression(ObjectiveType.BREAK_BLOCK, factionData, event.getBlock().getType(), player, count);
            ctx.getData().setInt("block_broken", count);
        }
    }
}
