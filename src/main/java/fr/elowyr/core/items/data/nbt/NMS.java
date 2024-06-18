package fr.elowyr.core.items.data.nbt;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMS
{
    public static final String ITEM_KEY = "item_key";
    public static final String ITEM_OWNER_KEY = "owner";
    public static final String SKULL_OWNER_KEY = "skull_owner";
    public static final String BLOCK_BROKEN_KEY = "block_broken";
    public static final String HITS_KEY = "hits";
    public static final String KILL_KEY = "kill";
    public static final String USES_KEY = "uses";
    public static final String SOLD_KEY = "sold";
    public static final String LAST_KILl_KEY = "last_kill";
    private static final String PLUGIN_TAG = "elowyr_items";
    
    public static ItemStack setNBT(final ItemStack stack, final NBTData data) {
        if (data == null) {
            return stack;
        }
        final net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = nms.getTag();
        if (tag == null) {
            nms.setTag(tag = new NBTTagCompound());
        }
        tag.set("elowyr_items", data.toNBT());
        return CraftItemStack.asCraftMirror(nms);
    }
    
    public static NBTData getNBT(final ItemStack stack) {
        final net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        final NBTTagCompound tag = nms.getTag();
        if (tag == null) {
            return null;
        }
        final NBTTagCompound sub = tag.getCompound("elowyr_items");
        if (sub == null) {
            return null;
        }
        return new NBTData(sub);
    }
    
    public static Object toString(final NBTBase base) {
        if (base instanceof NBTTagByte) {
            return ((NBTTagByte)base).g();
        }
        if (base instanceof NBTTagInt) {
            return ((NBTTagInt)base).d();
        }
        if (base instanceof NBTTagLong) {
            return ((NBTTagLong)base).d();
        }
        if (base instanceof NBTTagDouble) {
            return ((NBTTagDouble)base).g();
        }
        if (base instanceof NBTTagString) {
            return ((NBTTagString)base).a_();
        }
        return null;
    }
}
