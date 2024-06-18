package fr.elowyr.core.lang;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.interfaces.TypedMessage;
import fr.elowyr.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lang {

    private static Lang instance;
    private final Plugin plugin;
    private final File file;
    private Map<String, Object> root;

    public Lang(final Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "lang.yml");
    }
    
    public void load() {
        if (!this.file.exists()) {
            this.plugin.saveResource("lang.yml", false);
        }
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
            this.root = this.loadValues(config);
            this.plugin.getLogger().info("Lang loaded");
        }
        catch (Throwable ex) {
            ElowyrCore.severe("Failed to load lang");
            ex.printStackTrace();
        }
    }
    
    private Map<String, Object> loadValues(final ConfigurationSection section) {
        final Map<String, Object> data = new LinkedHashMap<>();
        for (final String key : section.getKeys(false)) {
            if (section.isString(key)) {
                data.put(key, Utils.color(section.getString(key)));
            }
            else if (section.isList(key)) {
                data.put(key, Utils.colorAll(section.getStringList(key)));
            }
            else {
                if (!section.isConfigurationSection(key)) {
                    continue;
                }
                final ConfigurationSection sub = section.getConfigurationSection(key);
                if (sub.contains("_type") && sub.isString("_type")) {
                    data.put(key, this.createTypedMessage(sub));
                }
                else {
                    data.put(key, this.loadValues(sub));
                }
            }
        }
        return data;
    }
    
    private Object createTypedMessage(final ConfigurationSection sub) {
        final String type = sub.getString("_type");
        if (type.toLowerCase().contains("action")) {
            return new ActionBarMessage(Utils.color(sub.getString("value")));
        }
        if (type.toLowerCase().contains("title")) {
            final String title = Utils.color(sub.getString("title"));
            final String subTitle = Utils.color(sub.getString("sub-title"));
            final int fadeIn = sub.getInt("fade-in", 100);
            final int stay = sub.getInt("stay", 100);
            final int fadeOut = sub.getInt("fade-out", 100);
            return new BigTitleMessage(title, subTitle, fadeIn, stay, fadeOut);
        }
        if (type.equalsIgnoreCase("none")) {
            return NoneMessage.INSTANCE;
        }
        if (type.equalsIgnoreCase("json")) {
            return new JsonMessage(sub.getString("value"));
        }
        return sub.getString("value");
    }
    
    public Object get(final String key) {
        Map<String, Object> current = this.root;
        final String[] names = key.split("\\.");
        for (int i = 0; i < names.length - 1; ++i) {
            final Object data = current.get(names[i]);
            if (!(data instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>)data;
        }
        return current.get(names[names.length - 1]);
    }
    
    public String getString(final String key) {
        final Object data = get().get(key);
        if (data instanceof String) {
            return (String)data;
        }
        if (data instanceof List) {
            return String.join("\n", (Iterable<? extends CharSequence>)data);
        }

        if (data instanceof TypedMessage) {
            final TypedMessage msg = (TypedMessage)data;
            return msg.toString();
        }
        return key;
    }
    
    public static void send(final CommandSender sender, final String key, final Object... replacements) {
        if (sender == null) {
            return;
        }
        final Object data = get().get(key);
        if (data == null) {
            sender.sendMessage(key);
        }
        else if (data instanceof String) {
            sender.sendMessage(Utils.replaceAll((String)data, replacements));
        }
        else if (data instanceof List) {
            sender.sendMessage(Utils.replaceAll(((List<?>) data).toArray(new String[0]), replacements));
        }
        else if (data instanceof TypedMessage) {
            if (sender instanceof Player) {
                ((TypedMessage)data).send((Player)sender, replacements);
            }
            else {
                sender.sendMessage(Utils.replaceAll(data.toString(), replacements));
            }
        }
    }
    
    public static void broadcast(final String key, final Object... replacements) {
        final Object data = get().get(key);
        if (data == null) {
            Bukkit.broadcastMessage(key);
        }
        else if (data instanceof String) {
            Bukkit.broadcastMessage(Utils.replaceAll((String)data, replacements));
        }
        else if (data instanceof List) {
            final String[] replaceAll;
            final String[] messages = replaceAll = Utils.replaceAll(((List<?>) data).toArray(new String[0]), replacements);
            for (final String message : replaceAll) {
                Bukkit.broadcastMessage(message);
            }
        }
        else if (data instanceof TypedMessage) {
            ((TypedMessage)data).broadcast(replacements);
        }
    }
    
    public static void set(final Lang instance) {
        Lang.instance = instance;
    }
    
    public static Lang get() {
        return Lang.instance;
    }

    public static Lang getInstance() {
        return instance;
    }

    public static void setInstance(Lang instance) {
        Lang.instance = instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return file;
    }

    public Map<String, Object> getRoot() {
        return root;
    }

    public void setRoot(Map<String, Object> root) {
        this.root = root;
    }
}
