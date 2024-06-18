package fr.elowyr.core.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemBuilder
{
    private final ItemStack stack;
    private final ItemMeta meta;
    
    public ItemBuilder(final ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();
    }
    
    public ItemBuilder lore(final List<String> lore) {
        this.meta.setLore(lore);
        return this;
    }
    
    public ItemBuilder displayName(final String displayName) {
        this.meta.setDisplayName(Utils.color(displayName));
        return this;
    }
    
    public ItemStack build() {
        this.stack.setItemMeta(this.meta);
        return this.stack;
    }
    
    public ItemBuilder addEnchant(final Enchantment ench, final int level) {
        if (this.meta instanceof EnchantmentStorageMeta) {
            ((EnchantmentStorageMeta)this.meta).addStoredEnchant(ench, level, true);
        }
        else {
            this.meta.addEnchant(ench, level, true);
        }
        return this;
    }
    
    public ItemBuilder addFlags(final ItemFlag[] flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder addFlag(final ItemFlag flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder owner(final String player) {
        if (this.meta instanceof SkullMeta) {
            ((SkullMeta)this.meta).setOwner(player);
        }
        return this;
    }
    
    public ItemBuilder setColor(final Color color) {
        if (this.meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)this.meta).setColor(color);
        }
        return this;
    }
    
    public static ItemBuilder newBuilder(final Material type, final int data) {
        return new ItemBuilder(new ItemStack(type, 1, (short)data));
    }

    public static ItemBuilder newBuilder(ItemStack stack) {
        return new ItemBuilder(stack);
    }

    public static String convertItemToJson(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_8_R3.NBTTagCompound compound = new NBTTagCompound();
        nmsItemStack.save(compound);
        return compound.toString();
    }
}
