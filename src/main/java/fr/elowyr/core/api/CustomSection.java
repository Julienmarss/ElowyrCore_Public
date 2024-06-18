package fr.elowyr.core.api;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

public class CustomSection extends MemorySection
{
    public CustomSection(final ConfigurationSection parent, final String path) {
        super(parent, path);
    }
}
