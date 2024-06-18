package fr.elowyr.core.data;

import fr.elowyr.core.utils.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class BlockData {
    private final Material type;

    private final Integer data;

    private BlockData(Material type, Integer data) {
        this.type = type;
        this.data = data;
    }

    public void set(Block b) {
        b.setType(this.type);
        if (this.data != null)
            b.setData(this.data.byteValue());
    }

    public static BlockData from(ConfigurationSection section) {
        if (section == null)
            return null;
        Material type = BukkitUtils.getType(section.getString("type"));
        Integer data = section.contains("damage") ? section.getInt("damage") : null;
        return new BlockData(type, data);
    }
}
