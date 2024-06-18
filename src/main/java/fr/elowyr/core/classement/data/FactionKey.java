package fr.elowyr.core.classement.data;

public enum FactionKey implements FieldKey {

    PVP("pvp"), 
    FARM("farm");
    
    private final String name;
    
    FactionKey(final String name) {
        this.name = name;
    }
    
    @Override
    public int getId() {
        return this.ordinal();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String format(final Double value) {
        return String.valueOf(value.intValue());
    }
    
    public static FactionKey fromName(final String name) {
        for (final FactionKey key : values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }
}
