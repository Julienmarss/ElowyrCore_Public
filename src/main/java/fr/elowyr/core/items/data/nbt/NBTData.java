package fr.elowyr.core.items.data.nbt;

import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;

public class NBTData {

    private final NBTData parent;
    private final NBTTagCompound compound;
    private boolean changed;

    public NBTData() {
        this(new NBTTagCompound());
    }
    
    public NBTData(final NBTTagCompound tag) {
        this(null, tag);
    }
    
    public NBTData(final NBTData parent, final NBTTagCompound tag) {
        this.parent = parent;
        this.compound = tag;
        this.changed = false;
    }

    public void setBoolean(final String key, final boolean value) {
        this.compound.setBoolean(key, value);
        this.setChanged(true);
    }

    public void setInt(final String key, final int value) {
        this.compound.setInt(key, value);
        this.setChanged(true);
    }

    public void setDouble(final String key, final double value) {
        this.compound.setDouble(key, value);
        this.setChanged(true);
    }

    public int addInt(final String key, final int value) {
        final int newValue = this.getInt(key) + value;
        this.setInt(key, newValue);
        return newValue;
    }

    public double addDouble(final String key, final double value) {
        final double newValue = this.getDouble(key) + value;
        this.setDouble(key, newValue);
        return newValue;
    }

    public int removeInt(final String key, final int value) {
        final int newValue = this.getInt(key) - value;
        this.setInt(key, newValue);
        return newValue;
    }
    
    public int getInt(final String key) {
        return this.compound.getInt(key);
    }

    public double getDouble(final String key) {
        return this.compound.getDouble(key);
    }

    public boolean getBoolean(final String key) {
        return this.compound.getBoolean(key);
    }

    public int getUnsignedInt(final String key) {
        final int value = this.getInt(key);
        return (value >= 0) ? value : (-value);
    }
    
    public void setLong(final String key, final long value) {
        this.compound.setLong(key, value);
        this.setChanged(true);
    }
    
    public long getLong(final String key) {
        return this.compound.getLong(key);
    }
    
    public void setString(final String key, final String value) {
        this.compound.setString(key, value);
        this.setChanged(true);
    }
    
    public String getString(final String key) {
        final NBTBase base = this.compound.get(key);
        return (base instanceof NBTTagString) ? ((NBTTagString)base).a_() : null;
    }
    
    public String getString(final String key, final String def) {
        final String value = this.getString(key);
        return (value != null) ? value : def;
    }
    
    public void remove(final String key) {
        this.compound.remove(key);
        this.setChanged(true);
    }
    
    public String replace(String line) {
        for (final Object name : this.compound.c()) {
            final String nameStr = String.valueOf(name);
            line = line.replace('{' + nameStr + '}', String.valueOf(NMS.toString(this.compound.get(nameStr))));
        }
        return line;
    }
    
    public void setChanged(final boolean changed) {
        this.changed = changed;
        if (this.parent != null) {
            this.parent.setChanged(true);
        }
    }
    
    public boolean isChanged() {
        return this.changed;
    }
    
    public NBTTagCompound toNBT() {
        return this.compound;
    }
    
    @Override
    public String toString() {
        return this.compound.toString();
    }
    
    public static NBTData ofStringArray(final String[] keys, final String[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("You must provide pairs of key-value");
        }
        final NBTData data = new NBTData();
        for (int i = 0; i < keys.length; ++i) {
            data.setString(keys[i], values[i]);
        }
        return data;
    }

}
