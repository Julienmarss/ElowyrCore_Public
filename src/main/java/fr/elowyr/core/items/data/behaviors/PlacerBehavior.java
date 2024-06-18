package fr.elowyr.core.items.data.behaviors;

import com.massivecraft.factions.FPlayers;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.tasks.PlaceTask;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.RegionUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlacerBehavior extends BasicBehavior {

    private int maxUses;
    private Material placeType;
    private byte placeData;
    private long period;
    private ConfigurationSection section;
    
    public PlacerBehavior() {
        super(UseType.RIGHT_CLICK_BLOCK);
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        this.section = section;
        this.placeType = BukkitUtils.getType(section.getString("place.type"));
        this.placeData = (byte)section.getInt("place.data", 0);
        this.period = section.getLong("period", 1000L);
        this.maxUses = section.getInt("max-uses", 50);
    }
    
    @Override
    public void getDefaultData(final NBTData data) {
        data.setInt("uses", 0);
    }
    
    @Override
    public boolean use(final ItemContext<?> ctx) {
        final PlayerInteractEvent e = (PlayerInteractEvent)ctx.getEvent();
        Block block = e.getClickedBlock();
        final Player p = ctx.getPlayer();
        final BlockFace face = this.section.getString("face").equalsIgnoreCase("block") ? e.getBlockFace() : BlockFace.valueOf(this.section.getString("face"));

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            e.setCancelled(true);
            return false;
        }

        if (RegionUtils.inArea(block.getLocation(), block.getWorld(), "warzone") ||
                RegionUtils.inArea(block.getLocation(),block.getWorld(), "spawn") || FPlayers.getInstance().getByPlayer(p).isInNeutralTerritory()) {
            p.sendMessage(Utils.color("&6&lElowyr &7◆ &fVous ne pouvez pas &cutiliser&f une &ebâtisseuse&f ici."));
            return false;
        }

        new PlaceTask(p, this.placeType, this.placeData, face, e.getClickedBlock().getRelative(face)).runTaskTimer(ElowyrCore.getInstance(), 0L, this.period);
        if (ctx.getData().addInt("uses", 1) >= this.maxUses) {
            ctx.useItem(1);
        }
        return false;
    }
}
