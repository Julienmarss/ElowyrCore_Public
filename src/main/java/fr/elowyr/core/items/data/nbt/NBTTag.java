package fr.elowyr.core.items.data.nbt;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTTag {

    public static ItemStack addNBTString(ItemStack item, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set(key, new NBTTagString(value));
        nmsItem.setTag(itemCompound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static ItemStack addNBTInteger(ItemStack item, String key, int value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set(key, new NBTTagInt(value));
        nmsItem.setTag(itemCompound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }


    public static String getNBTString(ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.hasTag()) {
            NBTTagCompound itemCompound = nmsItem.getTag();
            return itemCompound.getString(key);
        }
        return null;
    }

    public static Integer getNBTInteger(ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.hasTag()) {
            NBTTagCompound itemCompound = nmsItem.getTag();
            return itemCompound.getInt(key);
        }
        return null;
    }
}
