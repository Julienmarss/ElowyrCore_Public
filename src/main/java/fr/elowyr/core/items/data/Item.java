package fr.elowyr.core.items.data;

import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.utils.BukkitUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.ListIterator;

public class Item {

    private final String name;
    private ItemStack item;
    private boolean unbreakable;

    public Item(final String name) {
        this.name = name;
    }

    public void load(final ConfigurationSection section) {
        this.item = BukkitUtils.parseItemStack(section);
        this.unbreakable = section.getBoolean("unbreakable", false);
    }

    public String getName() {
        return this.name;
    }

    public boolean isInvalid() {
        return this.item == null;
    }

    public NBTData getDefaultData(final String owner) {
        return NBTData.ofStringArray(new String[] { "item_key", "owner" }, new String[] { this.name, owner });
    }

    public ItemStack build(final String owner) {
        return this.build(this.getDefaultData(owner), 1, (short)(-1));
    }

    public ItemStack build() {
        return this.build(this.getDefaultData(""), 1, (short)(-1));
    }

    public ItemStack build(final NBTData data, final int amount, final short durability) {
        ItemStack stack = CraftItemStack.asCraftCopy(this.item.clone());
        if (this.unbreakable) {
            final ItemMeta meta = stack.getItemMeta();
            meta.spigot().setUnbreakable(true);
            stack.setItemMeta(meta);
        }
        else if (durability >= 0) {
            stack.setDurability(durability);
        }
        stack = this.buildOnStack(stack, data, amount);
        return stack;
    }

    public ItemStack buildOnStack(ItemStack stack, final NBTData data, final int amount) {
        stack = NMS.setNBT(stack, data);
        stack.setAmount(amount);
        this.setLore(stack, data);
        return stack;
    }

    public void setLore(final ItemStack stack, final NBTData data) {
        final ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            final List<String> lore = meta.getLore();
            if (lore != null) {
                final ListIterator<String> iter = lore.listIterator();
                while (iter.hasNext()) {
                    iter.set(data.replace(iter.next()));
                }
                meta.setLore(lore);
            }
            if (meta.hasDisplayName()) {
                meta.setDisplayName(data.replace(meta.getDisplayName()));
            }
            if (meta instanceof SkullMeta) {
                final String skullOwner = data.getString("skull_owner");
                if (skullOwner != null) {
                    ((SkullMeta)meta).setOwner(skullOwner);
                }
            }
            stack.setItemMeta(meta);
        }
    }
}
