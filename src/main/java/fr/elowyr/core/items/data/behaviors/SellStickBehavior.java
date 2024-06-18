package fr.elowyr.core.items.data.behaviors;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.ShopGuiPlusHook;
import fr.elowyr.core.utils.VaultUtils;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellStickBehavior extends BasicBehavior {

    private int maxUses;
    private double multiplier;
    
    public SellStickBehavior() {
        super(UseType.RIGHT_CLICK_BLOCK);
    }
    
    @Override
    public void load(final ConfigurationSection section) {
        this.maxUses = section.getInt("max-uses");
        this.multiplier = section.getDouble("multiplier", 1.0);
    }
    
    @Override
    public void getDefaultData(final NBTData data) {
        data.setInt("sold", 0);
        data.setInt("uses", 0);
    }
    
    @Override
    public boolean use(final ItemContext<?> ctx) {
        final PlayerInteractEvent event = (PlayerInteractEvent)ctx.getEvent();
        final Player player = ctx.getPlayer();
        final Block block = event.getClickedBlock();
        final NBTData data = ctx.getData();
        if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.TRAPPED_CHEST)) {
            return false;
        }
        final Chest chest = (Chest)block.getState();
        final Inventory inventory = chest.getInventory();
        ctx.cancel();
        if (!BukkitUtils.canInteract(player, block)) {
            Lang.send(player, "listeners.sell-stick.cannot-access");
            return false;
        }
        double total;
        if (inventory instanceof DoubleChestInventory) {
            final DoubleChestInventory dc = (DoubleChestInventory)inventory;
            total = (this.sell(player, dc.getLeftSide()) + this.sell(player, dc.getRightSide())) * this.multiplier;
        }
        else {
            total = this.sell(player, inventory) * this.multiplier;
        }
        if (total > 0.0) {
            total *= ShopGuiPlusHook.getShopMultiplier(player);
            VaultUtils.depositMoney(player, total);
            Lang.send(player, "listeners.sell-stick.success", "money", String.format(Config.get().getFloatFormat(), total));
            if (data.addInt("uses", 1) >= this.maxUses) {
                ctx.useItem(1);
            }
        }
        return false;
    }
    
    private double sell(final Player p, final Inventory inv) {
        double total = 0.0;
        for (int i = 0; i < inv.getSize(); ++i) {
            final ItemStack stack = inv.getItem(i);
            if (stack != null) {
                if (!stack.getType().equals(Material.AIR)) {
                    final double price = ShopGuiPlusApi.getItemStackPriceSell(p, stack);
                    if (price > 0.0) {
                        total += price;
                        inv.setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
        if (total > 0.0 && inv.getHolder() instanceof BlockState) {
            ((BlockState)inv.getHolder()).update();
        }
        return total;
    }
}
