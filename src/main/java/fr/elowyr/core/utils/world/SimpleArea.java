package fr.elowyr.core.utils.world;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class SimpleArea
{
    private BukkitLocation pos1;
    private BukkitLocation pos2;
    private BukkitLocation min;
    private BukkitLocation max;
    private BukkitLocation middle;
    
    public void setPosition(final int pos, final Location loc) {
        this.setPosition(pos, new BukkitLocation(loc));
    }
    
    public void setPosition(final int pos, final BukkitLocation loc) {
        if (pos == 1) {
            this.pos1 = loc;
        }
        else if (pos == 2) {
            this.pos2 = loc;
        }
        this.recalculateArea();
    }

    public BukkitLocation getMiddle() {
        return middle;
    }

    public BukkitLocation getMin() {
        return this.min;
    }
    
    public BukkitLocation getMax() {
        return this.max;
    }
    
    public boolean isDefined() {
        return this.min != null && this.max != null && this.min.getWorldName().equals(this.max.getWorldName());
    }
    
    public Double distanceFromMiddleXZ(final Location loc) {
        if (this.middle == null || !this.middle.getWorldName().equals(loc.getWorld().getName())) {
            return null;
        }
        return this.middle.distanceXZ(loc);
    }
    
    private void recalculateArea() {
        if (this.pos1 != null && this.pos2 != null && this.pos1.getWorldName().equals(this.pos2.getWorldName())) {
            final String worldName = this.pos1.getWorldName();
            final int minX = Math.min(this.pos1.getX(), this.pos2.getX());
            final int minY = Math.min(this.pos1.getY(), this.pos2.getY());
            final int minZ = Math.min(this.pos1.getZ(), this.pos2.getZ());
            final int maxX = Math.max(this.pos1.getX(), this.pos2.getX());
            final int maxY = Math.max(this.pos1.getY(), this.pos2.getY());
            final int maxZ = Math.max(this.pos1.getZ(), this.pos2.getZ());
            this.min = new BukkitLocation(worldName, minX, minY, minZ);
            this.max = new BukkitLocation(worldName, maxX, maxY, maxZ);
            this.middle = new BukkitLocation(worldName, (maxX + minX) / 2, (maxY + minY) / 2, (maxZ + minZ) / 2);
        }
    }
    
    public boolean isInArea(final Location loc) {
        if (this.min == null || this.max == null) {
            return false;
        }
        if (!loc.getWorld().getName().equals(this.min.getWorldName())) {
            return false;
        }
        final int x = loc.getBlockX();
        final int y = loc.getBlockY();
        final int z = loc.getBlockZ();
        return x >= this.min.getX() && x <= this.max.getX() && y >= this.min.getY() && y <= this.max.getY() && z >= this.min.getZ() && z <= this.max.getZ();
    }
    
    public void write(final ConfigurationSection section) {
        if (this.pos1 != null) {
            this.pos1.write(section.createSection("pos1"));
        }
        if (this.pos2 != null) {
            this.pos2.write(section.createSection("pos2"));
        }
    }
    
    public static SimpleArea from(final ConfigurationSection section) {
        if (section == null) {
            return new SimpleArea();
        }
        final SimpleArea area = new SimpleArea();
        area.setPosition(1, BukkitLocation.from(section.getConfigurationSection("pos1")));
        area.setPosition(2, BukkitLocation.from(section.getConfigurationSection("pos2")));
        return area;
    }
}
