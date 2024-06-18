package fr.elowyr.core.classement.data;

import fr.elowyr.core.config.Config;
import fr.elowyr.core.utils.TimeUtils;

import java.util.function.*;

public enum UserKey implements FieldKey
{
    JOBS_POINTS("points", UserKey::formatDouble), 
    KILL("kill", UserKey::formatInt), 
    DEATH("death", UserKey::formatInt), 
    MOB_KILL("mob_kill", UserKey::formatInt), 
    TIME_PLAYED("time_played", value -> TimeUtils.format(value.longValue())),
    BLOCK_BROKEN("block_broken", UserKey::formatInt), 
    VOTES("votes", UserKey::formatInt),
    GAMBLING("gambling", UserKey::formatInt),
    JOYAUX("joyaux", UserKey::formatInt),
    VOTE_COUNT("votecount", UserKey::formatInt);

    private final String name;
    private final Function<Double, String> formatter;
    
    UserKey(final String name, final Function<Double, String> formatter) {
        this.name = name;
        this.formatter = formatter;
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
        return this.formatter.apply(value);
    }
    
    public static UserKey fromName(final String name) {
        for (final UserKey key : values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }
    
    private static String formatDouble(final Double value) {
        return String.format(Config.get().getFloatFormat(), value);
    }
    
    private static String formatInt(final Double value) {
        return String.valueOf(value.intValue());
    }
}
