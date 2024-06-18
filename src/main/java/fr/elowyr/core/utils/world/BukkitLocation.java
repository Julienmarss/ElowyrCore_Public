package fr.elowyr.core.utils.world;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class BukkitLocation
{
    private String worldName;
    private int x;
    private int y;
    private int z;
    private World world;
    
    public BukkitLocation(final Location loc) {
        this.set(loc);
    }
    
    public BukkitLocation(final String worldName, final int x, final int y, final int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public void set(final Location loc) {
        this.world = loc.getWorld();
        this.worldName = this.world.getName();
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }
    
    public World getWorld() {
        if (this.world == null) {
            this.world = Bukkit.getWorld(this.worldName);
        }
        return this.world;
    }
    
    public Location toLocation() {
        return new Location(this.getWorld(), this.x, this.y, this.z);
    }
    
    public void write(final ConfigurationSection section) {
        section.set("world", this.worldName);
        section.set("x", this.x);
        section.set("y", this.y);
        section.set("z", this.z);
    }
    
    public double distanceSquaredXZ(final Location loc) {
        final double xx = this.x - loc.getBlockX() + 0.5;
        final double zz = this.z - loc.getBlockZ() + 0.5;
        return xx * xx + zz * zz;
    }
    
    public Double distanceXZ(final Location loc) {
        if (!this.worldName.equals(loc.getWorld().getName())) {
            return null;
        }
        return Math.sqrt(this.distanceSquaredXZ(loc));
    }
    
    public long getChunkXZ() {
        final long x = (long)(this.x >> 4) & 0xFFFFFFFFL;
        final long z = (long)(this.z >> 4) & 0xFFFFFFFFL;
        return x | z << 32;
    }
    
    public boolean isSameXYZ(final Location loc) {
        return this.x == loc.getBlockX() && this.y == loc.getBlockY() && this.z == loc.getBlockZ();
    }
    
    public static long getChunkKey(final Chunk chunk) {
        final long x = (long)chunk.getX() & 0xFFFFFFFFL;
        final long z = (long)chunk.getZ() & 0xFFFFFFFFL;
        return x | z << 32;
    }
    
    public static BukkitLocation from(final ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        final String worldName = section.getString("world");
        final int x = section.getInt("x");
        final int y = section.getInt("y");
        final int z = section.getInt("z");
        return new BukkitLocation(worldName, x, y, z);
    }
}
