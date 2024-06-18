package fr.elowyr.core.utils;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.api.CustomSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class ConfigUtils {

    public static Map<String, Long> getStringLongMap(final ConfigurationSection section) {
        if (section == null) {
            return Collections.emptyMap();
        }
        final Map<String, Long> result = new HashMap<>();
        for (final String key : section.getKeys(false)) {
            result.put(key, section.getLong(key));
        }
        return result;
    }
    
    public static List<DayOfWeek> getDayList(final ConfigurationSection section, final String path) {
        if (!section.contains(path)) {
            return null;
        }
        if (section.isList(path)) {
            final List<DayOfWeek> days = new LinkedList<>();
            for (final String dayName : section.getStringList(path)) {
                try {
                    days.add(DayOfWeek.valueOf(dayName));
                }
                catch (Throwable ex) {
                    ElowyrCore.warn("Unknown day " + dayName);
                }
            }
            return days;
        }
        try {
            return Collections.singletonList(DayOfWeek.valueOf(section.getString(path)));
        }
        catch (Throwable ex2) {
            ElowyrCore.warn("Unknown day " + section.getString(path));
            return null;
        }
    }
    
    public static Stream<ConfigurationSection> getSectionList(final ConfigurationSection root, final String path) {
        return root.getMapList(path).stream().map(new Function<Map<?, ?>, ConfigurationSection>() {
            private final AtomicInteger index = new AtomicInteger(0);
            
            @Override
            public ConfigurationSection apply(final Map<?, ?> map) {
                final MemorySection section = new CustomSection(root, String.valueOf(this.index.getAndIncrement()));
                convertMapsToSections(map, section);
                return section;
            }
        });
    }
    
    private static void convertMapsToSections(final Map<?, ?> input, final ConfigurationSection section) {
        for (final Map.Entry<?, ?> entry : input.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();
            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>)value, section.createSection(key));
            }
            else {
                section.set(key, value);
            }
        }
    }
}
