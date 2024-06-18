package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FarmAxeBehavior extends BasicBehavior {

    public FarmAxeBehavior() {
        super(UseType.LEFT_CLICK_BLOCK);
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("block_broken", 0);
    }

    @Override
    public boolean use(ItemContext<?> context) {
        final PlayerInteractEvent event = (PlayerInteractEvent) context.getEvent();
        Block block = event.getClickedBlock();
        if (block == null) return false;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        ItemStack newItemstack = getMultiToolsType(block.getType(), itemStack);
        if (itemStack.getType() != newItemstack.getType()) {
            player.setItemInHand(newItemstack);
        }
        return true;
    }

    private ItemStack getMultiToolsType(Material type, ItemStack item) {
        Material newType;
        if (type.name().contains("WOOD") || type.name().contains("LOG")
                || (type.name().contains("DOOR") && type != Material.IRON_DOOR && type != Material.IRON_TRAPDOOR)
                || type == Material.JUKEBOX
                || type == Material.BOOKSHELF
                || type == Material.CHEST
                || type == Material.TRAPPED_CHEST
                || type == Material.WORKBENCH
                || type == Material.MELON_BLOCK
                || type == Material.PUMPKIN
                || (type.name().contains("FENCE") && type != Material.IRON_FENCE)) {
            newType = Material.DIAMOND_AXE;
        } else if (type == Material.GRASS
                || type == Material.SAND
                || type == Material.DIRT
                || type == Material.GRAVEL
                || type == Material.SOUL_SAND) {

            newType = Material.DIAMOND_SPADE;
        } else {
            newType = Material.DIAMOND_PICKAXE;
        }
        ItemStack newItemStack = item.clone();
        newItemStack.setType(newType);
        return newItemStack;
    }
}
