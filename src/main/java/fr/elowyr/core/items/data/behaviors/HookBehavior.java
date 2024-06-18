package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Openable;

public class HookBehavior extends BasicBehavior {

    private int maxUses;

    public HookBehavior() {
        super(UseType.RIGHT_CLICK_BLOCK);
    }

    @Override
    public void load(final ConfigurationSection section) {
        super.load(section);
        this.maxUses = section.getInt("maxUses", 5);
    }

    @Override
    public void getDefaultData(final NBTData data) {
        data.setInt("uses", 0);
    }

    @Override
    public boolean use(ItemContext<?> context) {
        final PlayerInteractEvent event = (PlayerInteractEvent) context.getEvent();
        Block block = event.getClickedBlock();
        if(block == null) {
            return false;
        }

        final Block down = block.getRelative(BlockFace.DOWN);
        if(block.getType().name().toLowerCase().contains("door") && down.getType().name().toLowerCase().contains("door")) block = down;
        BlockState blockState = block.getState();
        if (((Openable) blockState.getData()).isOpen()) {
            return false;
        }
        if (context.getData().addInt("uses", 1) >= this.maxUses) {
            context.useItem(1);
            context.getData().setInt("uses", 0);
            context.getPlayer().playSound(context.getPlayer().getLocation(), Sound.ITEM_BREAK, 5, 5);
        }

        ((Openable)blockState.getData()).setOpen(true);
        blockState.update();
        event.setCancelled(true);
        return true;
    }
}
