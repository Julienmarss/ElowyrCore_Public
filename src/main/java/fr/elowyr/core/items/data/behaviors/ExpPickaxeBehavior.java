package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.InventoryUtils;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ExpPickaxeBehavior extends BasicBehavior {

    public ExpPickaxeBehavior() {
        super(UseType.BLOCK_BREAK);
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("block_broken", 0);
        data.setInt("durability", 3500);
    }

    @Override
    public boolean use(ItemContext<?> context) {
        final BlockBreakEvent event = (BlockBreakEvent) context.getEvent();
        Block block = event.getBlock();
        if (block.getType().name().contains("ORE") && !block.hasMetadata("placed")) {
            final int random = Utils.randomInt(0, 100);
            if (random <= 20) {
                int level = new Random().nextInt(2) + 1;
                Player player = event.getPlayer();
                giveBottleXp(player, level);
                context.getData().setInt("block_broken", context.getData().getInt("block_broken") + 1);
            }
        }
        if (context.getData().getInt("durability") == 1) {
            InventoryUtils.decrementItem(context.getPlayer(), context.getPlayer().getItemInHand(), 1);
        }
        context.getData().setInt("durability", context.getData().getInt("durability") - 1);
        return false;
    }

    private void giveBottleXp(Player player, int total) {
        ItemStack bottleXp = new ItemStack(Material.EXP_BOTTLE, total);
        HashMap<Integer, ItemStack> leftOverItems = player.getInventory().addItem(bottleXp);
        if (!leftOverItems.isEmpty())
            for (Map.Entry<Integer, ItemStack> one : leftOverItems.entrySet()) {
                player.getWorld().dropItemNaturally(player.getLocation(), one.getValue());
            }
        Lang.send(player, "listeners.bottle-xp", "total", total);
    }
}
