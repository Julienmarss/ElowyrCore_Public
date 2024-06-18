package fr.elowyr.core.data;


import fr.elowyr.core.lang.Lang;

public enum BoostType
{
    EXPERIENCE(new String[] { "experience", "exp", "xp" }), 
    MONEY(new String[] { "money", "$" }), 
    POINTS(new String[] { "points", "point", "pts", "pt" });
    
    private final String[] names;
    
    private BoostType(final String[] names) {
        this.names = names;
    }
    
    public String getLang() {
        return Lang.get().getString("boost.types." + this.name().toLowerCase());
    }
    
    public static BoostType getByType(final String name) {
        for (final BoostType type : values()) {
            for (final String typeName : type.names) {
                if (typeName.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        return null;
    }
}
