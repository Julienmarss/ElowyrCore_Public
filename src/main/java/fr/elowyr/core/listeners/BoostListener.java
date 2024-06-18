package fr.elowyr.core.listeners;

import com.google.common.collect.Lists;
import fr.elowyr.core.ElowyrCore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class BoostListener implements Listener {

    private List<Material> WHITELISTED_BLOCKS = Lists.newArrayList(Material.CROPS, Material.COCOA, Material.POTATO, Material.CARROT, Material.NETHER_WARTS, Material.SOIL, Material.MELON_BLOCK, Material.PUMPKIN);

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        //Ce bloc ne peut pas être affecté par l'anti boost
        if(this.WHITELISTED_BLOCKS.contains(block.getType())) {
            return;
        }
        block.setMetadata("anti-boost", new FixedMetadataValue(ElowyrCore.getInstance(), true));
    }

}
