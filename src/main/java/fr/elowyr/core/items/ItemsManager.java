package fr.elowyr.core.items;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.behaviors.FarmArmorBehavior;
import fr.elowyr.core.items.data.ArmorItem;
import fr.elowyr.core.items.data.ArmorPartItem;
import fr.elowyr.core.items.data.Item;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NBTData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ItemsManager {

    private static ItemsManager instance;
    private final File file;
    private UsableItem godDager = new UsableItem("god_dager", "god_dager");
    private UsableItem godBow = new UsableItem("god_bow", "god_bow");
    private UsableItem godRod = new UsableItem("god_rod", null);
    private UsableItem godChestplate = new UsableItem("god_chestplate", null);
    private UsableItem scepter = new UsableItem("scepter", "scepter");
    private UsableItem harvester = new UsableItem("harvester", "harvester");
    private UsableItem exp_pickaxe = new UsableItem("exp_pickaxe", "exp_pickaxe");
    private UsableItem farming_sword_1 = new UsableItem("farming_sword_1", "farming_sword");
    private UsableItem farming_sword_2 = new UsableItem("farming_sword_2", "farming_sword");
    private UsableItem farming_sword_3 = new UsableItem("farming_sword_3", "farming_sword");

    private UsableItem farm_axe = new UsableItem("farm_axe", "farm_axe");
    private UsableItem hook = new UsableItem("hook", "hook");
    private UsableItem potioncounter = new UsableItem("potioncounter", "potioncounter");
    private UsableItem armorcounter = new UsableItem("armorcounter", "armorcounter");
    private final ArmorPartItem FARM_HELMET = new ArmorPartItem("farm_helmet");
    private final ArmorPartItem FARM_CHESTPLATE = new ArmorPartItem("farm_chestplate");
    private final ArmorPartItem FARM_LEGGINGS = new ArmorPartItem("farm_leggings");
    private final ArmorPartItem FARM_BOOTS = new ArmorPartItem("farm_boots");
    public final ArmorItem FARM_ARMOR = new ArmorItem("farm", FarmArmorBehavior.INSTANCE, FARM_HELMET, FARM_CHESTPLATE, FARM_LEGGINGS, FARM_BOOTS);
    public final List<UsableItem> ALL = new LinkedList<>(Arrays.asList(godDager, godBow, godRod, godChestplate, scepter, FARM_HELMET, FARM_CHESTPLATE, FARM_LEGGINGS, FARM_BOOTS, harvester, farm_axe, hook, potioncounter, armorcounter, exp_pickaxe, farming_sword_1, farming_sword_2, farming_sword_3));
    private final List<UsableItem> LOADABLE = Arrays.asList(godDager, godBow, godRod, godChestplate, scepter, harvester, FARM_ARMOR, farm_axe, hook, potioncounter, armorcounter, exp_pickaxe, farming_sword_1, farming_sword_2, farming_sword_3);

    public ItemsManager() {
        instance = this;
        this.file = new File(ElowyrCore.getInstance().getDataFolder(), "items.yml");
        this.loadItems();
    }

    public void loadItems() {
        if (!this.file.exists()) {
            ElowyrCore.getInstance().saveResource("items.yml", false);
        }
        YamlConfiguration config;
        try {
            config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
        }
        catch (Throwable ex) {
            ElowyrCore.severe("Failed to load config");
            ex.printStackTrace();
            config = new YamlConfiguration();
        }
        this.loadAll(config);
    }

    public UsableItem getByName(final String name) {
        return this.ALL.stream().filter(item -> item.getName().equals(name)).findFirst().orElse(null);
    }

    public UsableItem getByItemStack(final NBTData data) {
        if (data == null) {
            return null;
        }
        return getByName(data.getString("item_key"));
    }

    public void clear() {
        this.ALL.clear();
        this.ALL.addAll(this.LOADABLE);
    }

    public void loadAll(final ConfigurationSection config) {
        final ConfigurationSection root = config.getConfigurationSection("items");
        final ConfigurationSection custom = config.getConfigurationSection("custom-items");
        if (root != null) {
            for (final Item item : this.LOADABLE) {
                final ConfigurationSection itemSection = root.getConfigurationSection(item.getName());
                if (itemSection == null) {
                    ElowyrCore.warn("Item " + item.getName() + " not defined");
                }
                else {
                    item.load(itemSection);
                }
            }
        }
        if (custom != null) {
            for (final String name : custom.getKeys(false)) {
                final ConfigurationSection itemSection = custom.getConfigurationSection(name);
                if (itemSection == null) {
                    ElowyrCore.warn("Invalid section custom-items." + name);
                }
                else {
                    final String behavior = itemSection.getString("behavior", null);
                    final UsableItem item2 = new UsableItem(name, behavior);
                    item2.load(itemSection);
                    this.ALL.add(item2);
                }
            }
        }
    }

    public static ItemsManager getInstance() {
        return instance;
    }

    public static void setInstance(ItemsManager instance) {
        ItemsManager.instance = instance;
    }

    public File getFile() {
        return file;
    }

    public UsableItem getGodDager() {
        return godDager;
    }

    public void setGodDager(UsableItem godDager) {
        this.godDager = godDager;
    }

    public UsableItem getGodBow() {
        return godBow;
    }

    public void setGodBow(UsableItem godBow) {
        this.godBow = godBow;
    }

    public UsableItem getGodRod() {
        return godRod;
    }

    public void setGodRod(UsableItem godRod) {
        this.godRod = godRod;
    }

    public UsableItem getGodChestplate() {
        return godChestplate;
    }

    public void setGodChestplate(UsableItem godChestplate) {
        this.godChestplate = godChestplate;
    }

    public UsableItem getScepter() {
        return scepter;
    }

    public void setScepter(UsableItem scepter) {
        this.scepter = scepter;
    }

    public UsableItem getHarvester() {
        return harvester;
    }

    public void setHarvester(UsableItem harvester) {
        this.harvester = harvester;
    }

    public UsableItem getExp_pickaxe() {
        return exp_pickaxe;
    }

    public void setExp_pickaxe(UsableItem exp_pickaxe) {
        this.exp_pickaxe = exp_pickaxe;
    }

    public UsableItem getFarming_sword_1() {
        return farming_sword_1;
    }

    public void setFarming_sword_1(UsableItem farming_sword_1) {
        this.farming_sword_1 = farming_sword_1;
    }

    public UsableItem getFarming_sword_2() {
        return farming_sword_2;
    }

    public void setFarming_sword_2(UsableItem farming_sword_2) {
        this.farming_sword_2 = farming_sword_2;
    }

    public UsableItem getFarming_sword_3() {
        return farming_sword_3;
    }

    public void setFarming_sword_3(UsableItem farming_sword_3) {
        this.farming_sword_3 = farming_sword_3;
    }

    public UsableItem getFarm_axe() {
        return farm_axe;
    }

    public void setFarm_axe(UsableItem farm_axe) {
        this.farm_axe = farm_axe;
    }

    public UsableItem getHook() {
        return hook;
    }

    public void setHook(UsableItem hook) {
        this.hook = hook;
    }

    public ArmorPartItem getFARM_HELMET() {
        return FARM_HELMET;
    }

    public ArmorPartItem getFARM_CHESTPLATE() {
        return FARM_CHESTPLATE;
    }

    public ArmorPartItem getFARM_LEGGINGS() {
        return FARM_LEGGINGS;
    }

    public ArmorPartItem getFARM_BOOTS() {
        return FARM_BOOTS;
    }

    public ArmorItem getFARM_ARMOR() {
        return FARM_ARMOR;
    }

    public List<UsableItem> getALL() {
        return ALL;
    }

    public List<UsableItem> getLOADABLE() {
        return LOADABLE;
    }
}
