package fr.elowyr.core.utils;

import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.items.ItemsManager;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Iterator;

public class ShopGuiPlusHook {

    public static void sellItems(final Player player, final Collection<ItemStack> items, final String message) {
        double money = 0.0;
        final Iterator<ItemStack> drops = items.iterator();
        while (drops.hasNext()) {
            final ItemStack stack = drops.next();
            final double price = ShopGuiPlusApi.getItemStackPriceSell(player, stack);
            if (price > 0.0) {
                drops.remove();
                money += price;
            }
        }
        if (money > 0.0) {
            VaultUtils.depositMoney(player, money);
            //BukkitUtils.sendActionBar(player, message.replace("money", String.format(Config.get().getFloatFormat(), money)));
            Lang.send(player, message, "money", String.format(Config.get().getFloatFormat(), money));
        }
    }

    public static void sellItems(final Player player, final Collection<ItemStack> items) {
        double money = 0.0;
        final Iterator<ItemStack> drops = items.iterator();
        while (drops.hasNext()) {
            final ItemStack stack = drops.next();
            double price = ShopGuiPlusApi.getItemStackPriceSell(player, stack);
            if (price > 0.0) {
                drops.remove();
                money += price;
                money *= ShopGuiPlusHook.getShopMultiplier(player);
            }
        }
        if (money > 0.0) {
            VaultUtils.depositMoney(player, money);
        }
    }

    public static void sellItems(final Player player, final Collection<ItemStack> items, int amount, final String message) {
        double money = 0.0;
        final Iterator<ItemStack> drops = items.iterator();
        while (drops.hasNext()) {
            final ItemStack stack = drops.next();
            stack.setAmount(amount);
            double price = ShopGuiPlusApi.getItemStackPriceSell(player, stack);
            if (price > 0.0) {
                drops.remove();
                money += price;
                money *= ShopGuiPlusHook.getShopMultiplier(player);
            }
        }
        if (money > 0.0) {
            VaultUtils.depositMoney(player, money);
            BukkitUtils.sendActionBar(player, Lang.get().getString(message).replace("%money%", String.format(Config.get().getFloatFormat(), (money * amount))));
        }
    }

    public static void sellItems(final Player player, final Collection<ItemStack> items, final String message, int amount) {
        double money = 0.0;
        final Iterator<ItemStack> drops = items.iterator();
        while (drops.hasNext()) {
            final ItemStack stack = drops.next();
            stack.setAmount(amount);
            double price = ShopGuiPlusApi.getItemStackPriceSell(player, stack);
            if (price > 0.0) {
                drops.remove();
                money += price;
                money *= ShopGuiPlusHook.getShopMultiplier(player);
            }
        }
        if (money > 0.0) {
            VaultUtils.depositMoney(player, money);
            Lang.send(player, message, "money", String.format(Config.get().getFloatFormat(), money));
        }
    }

    public static double getShopMultiplier(final Player p) {
        final ItemStack helmet = p.getInventory().getHelmet();
        if (helmet == null || helmet.getType().equals(Material.AIR)) {
            return 1.0;
        }
        final UsableItem item = ItemsManager.getInstance().getByItemStack(NMS.getNBT(helmet));
        if (item == null || item.getShopMultiplier() == null) {
            return 1.0;
        }
        return item.getShopMultiplier();
    }

}
