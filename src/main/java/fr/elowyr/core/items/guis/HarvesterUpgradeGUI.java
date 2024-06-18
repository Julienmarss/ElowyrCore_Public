package fr.elowyr.core.items.guis;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.menu.ClickAction;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import fr.elowyr.core.utils.menu.items.type.CloseItem;
import fr.elowyr.core.utils.menu.menus.Size;
import fr.elowyr.core.utils.menu.menus.VirtualGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class HarvesterUpgradeGUI extends VirtualGUI {

    public HarvesterUpgradeGUI(NBTData data, Player player, ItemContext<?> ctx) {
        super(Lang.get().getString("harvester.gui.name"), Size.CINQ_LIGNE);

        double grain = data.getDouble("grains");

        int[] slots = new int[]{0, 1, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 43, 44};

        this.getBorders(this, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE, 1).displayName("§f").build(), slots);

        this.setItem(4, new VirtualItem(ItemBuilder.newBuilder(Material.SKULL_ITEM, 3).owner(player.getName())
                .displayName(Utils.color("&eInformations &7▸ &6" + player.getName()))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&f⋄ &7Pseudo: &f" + player.getName(),
                        "&f⋄ &7Grains: &f" + String.format("%.2f", grain)
                ))).build()));

        VirtualItem hasteitem = data.getBoolean("haste") ? new VirtualItem(ItemBuilder.newBuilder(Material.GOLD_PICKAXE, 0)
                .displayName(Lang.get().getString("harvester.gui.haste.name"))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&6&l▏ &fDébloqué: §a✔",
                        "&f"
                ))).build()) :
                new VirtualItem(ItemBuilder.newBuilder(Material.GOLD_PICKAXE, 0).displayName(Lang.get().getString("harvester.gui.haste.name"))
                        .lore(Utils.colorAll(Arrays.asList(
                                "&f",
                                "&6&l▏ &fDébloqué: §c✘",
                                "&f",
                                "&fPrix: &a" + data.getInt("hasteprice") + " grains"
                        ))).build()).onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (grain < data.getInt("hasteprice")) {
                            Lang.send(player, "harvester.grains.dont-have");
                            return;
                        }
                        data.setDouble("grains", grain - data.getInt("hasteprice"));
                        data.setBoolean("haste", true);
                        ctx.updateHand();
                        player.closeInventory();
                        Lang.send(player, "harvester.grains.success.haste");
                    }
                });

        String pourcentageKey = data.getDouble("key_finder") == 0 ? "0%" : data.getDouble("key_finder") == 1 ? "0.1%" : "0.2%";
        String pourcentageNextKey = data.getDouble("key_finder") + 1 == 1 ? "0.1%" : "0.2%";

        VirtualItem keyfinderitem = data.getDouble("key_finder") == 2 ? new VirtualItem(ItemBuilder.newBuilder(Material.PAPER, 0)
                .displayName(Lang.get().getString("harvester.gui.key_finder.name"))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&6&l▏&fNiveau actuel: &e" + pourcentageKey + " §a✔",
                        "&f"
                ))).build()) :
                new VirtualItem(ItemBuilder.newBuilder(Material.PAPER, 0).displayName(Lang.get().getString("harvester.gui.key_finder.name"))
                        .lore(Utils.colorAll(Arrays.asList(
                                "&f",
                                "&6&l▏&fNiveau actuel: &e" + pourcentageKey,
                                "&6&l▏&fProchain niveau: &e" + pourcentageNextKey,
                                "&f",
                                "&fPrix: &a" + data.getDouble("key_finderprice") + " grains"
                        ))).build()).onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (grain < data.getDouble("key_finderprice")) {
                            Lang.send(player, "harvester.grains.dont-have");
                            return;
                        }
                        data.setDouble("grains", grain - data.getDouble("key_finderprice"));
                        if (data.getDouble("key_finder") != 2) {
                            int price = 100;
                            switch (data.getInt("key_finder")) {
                                case 0:
                                    price = ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.KEYFINDER.LEVEL-2");
                                    data.setDouble("key_finder", 1);
                                    break;
                                case 1:
                                    price = ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.KEYFINDER.LEVEL-3");
                                    data.setDouble("key_finder", 2);
                                    break;
                            }
                            data.setDouble("key_finderprice", price);
                        }
                        ctx.updateHand();
                        player.closeInventory();
                        Lang.send(player, "harvester.grains.success.key_finder");
                    }
                });
        String pourcentage = data.getDouble("spawner_finder") == 0 ? "0%" : data.getDouble("spawner_finder") == 1 ? "0.2%" : "0.3%";
        String pourcentageNext = data.getDouble("spawner_finder") + 1 == 1 ? "0.2%" : "0.3%";

        VirtualItem spfinderitem = data.getDouble("spawner_finder") == 2 ? new VirtualItem(ItemBuilder.newBuilder(Material.MOB_SPAWNER, 0)
                .displayName(Lang.get().getString("harvester.gui.spawner_finder.name"))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&6&l▏&fNiveau actuel: &e" + pourcentage + " §a✔",
                        "&f"
                ))).build()) :
                new VirtualItem(ItemBuilder.newBuilder(Material.MOB_SPAWNER, 0).displayName(Lang.get().getString("harvester.gui.spawner_finder.name"))
                        .lore(Utils.colorAll(Arrays.asList(
                                "&f",
                                "&6&l▏&fNiveau actuel: &e" + pourcentage,
                                "&6&l▏&fProchain niveau: &e" + pourcentageNext,
                                "&f",
                                "&fPrix: &a" + data.getDouble("spawner_finderprice") + " grains"
                        ))).build()).onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (grain < data.getDouble("spawner_finderprice")) {
                            Lang.send(player, "harvester.grains.dont-have");
                            return;
                        }
                        data.setDouble("grains", grain - data.getDouble("spawner_finderprice"));
                        if (data.getDouble("spawner_finder") != 2) {
                            double price = 100;
                            double finder = data.getDouble("spawner_finder");

                            switch ((int) finder) {
                                case 0:
                                    price = ElowyrCore.getInstance().getConfig().getDouble("HOUE.PRICE.SPAWNERFINDER.LEVEL-2");
                                    data.setDouble("spawner_finder", 1);
                                    break;
                                case 1:
                                    price = ElowyrCore.getInstance().getConfig().getDouble("HOUE.PRICE.SPAWNERFINDER.LEVEL-3");
                                    data.setDouble("spawner_finder", 2);
                                    break;
                            }

                            data.setDouble("spawner_finderprice", price);
                        }
                        ctx.updateHand();
                        player.closeInventory();
                        Lang.send(player, "harvester.grains.success.spawner_finder");
                    }
                });

        VirtualItem speeditem = data.getInt("speed") == 2 ? new VirtualItem(ItemBuilder.newBuilder(Material.DIAMOND_BOOTS, 0)
                .displayName(Lang.get().getString("harvester.gui.speed.name"))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&6&l▏&fNiveau actuel: &eVitesse " + data.getInt("speed") + " §a✔",
                        "&f"
                ))).build()) :
                new VirtualItem(ItemBuilder.newBuilder(Material.DIAMOND_BOOTS, 0).displayName(Lang.get().getString("harvester.gui.speed.name"))
                        .lore(Utils.colorAll(Arrays.asList(
                                "&f",
                                "&6&l▏&fNiveau actuel: &eVitesse " + data.getInt("speed"),
                                "&6&l▏&fProchain niveau: &eVitesse " + (data.getInt("speed") + 1),
                                "&f",
                                "&fPrix: &a" + data.getInt("speedprice") + " grains"
                        ))).build()).onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (grain < data.getInt("speedprice")) {
                            Lang.send(player, "harvester.grains.dont-have");
                            return;
                        }
                        data.setDouble("grains", grain - data.getInt("speedprice"));
                        if (data.getInt("speed") != 2) {
                            int price = 100;
                            switch (data.getInt("speed")) {
                                case 0:
                                    price = ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.SPEED.LEVEL-2");
                                    data.setInt("speed", 1);
                                    break;
                                case 1:
                                    data.setInt("speed", 2);
                                    break;
                            }
                            data.setInt("speedprice", price);
                        }
                        ctx.updateHand();
                        player.closeInventory();
                        Lang.send(player, "harvester.grains.success.speed", "level", data.getInt("speed"));
                    }
                });

        VirtualItem autosellitem = data.getBoolean("autosell") ? new VirtualItem(ItemBuilder.newBuilder(Material.MINECART, 0)
                .displayName(Lang.get().getString("harvester.gui.autosell.name"))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&6&l▏&fDébloqué " + (data.getBoolean("autosell") ? "§a✔" : "§c✘"),
                        "&f"
                ))).build()) :
                new VirtualItem(ItemBuilder.newBuilder(Material.MINECART, 0).displayName(Lang.get().getString("harvester.gui.autosell.name"))
                        .lore(Utils.colorAll(Arrays.asList(
                                "&f",
                                "&6&l▏&fDébloqué " + (data.getBoolean("autosell") ? "§a✔" : "§c✘"),
                                "&f",
                                "&fPrix: &a" + data.getInt("autosellprice") + " grains"
                        ))).build()).onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (grain < data.getInt("autosellprice")) {
                            Lang.send(player, "harvester.grains.dont-have");
                            return;
                        }
                        data.setDouble("grains", grain - data.getInt("autosellprice"));
                        data.setBoolean("autosell", true);
                        data.setString("place_autosell", "§a✔");
                        ctx.updateHand();
                        player.closeInventory();
                        Lang.send(player, "harvester.grains.success.autosell");
                    }
                });

        int radius = data.getInt("radius") == -1 ? 3 : data.getInt("radius") == -2 ? 5 : data.getInt("radius") == -3 ? 7 : 1;
        VirtualItem radiusitem = radius == 7 ? new VirtualItem(ItemBuilder.newBuilder(Material.BARRIER, 0)
                .displayName(Lang.get().getString("harvester.gui.radius.name"))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&6&l▏ &fVotre rayon: &e" + radius + "x" + radius + " §a✔",
                        "&f"
                ))).build()) :
                new VirtualItem(ItemBuilder.newBuilder(Material.TNT, 0).displayName(Lang.get().getString("harvester.gui.radius.name"))
                        .lore(Utils.colorAll(Arrays.asList(
                                "&f",
                                "&6&l▏ &fVotre rayon: &e" + radius + "x" + radius,
                                "&6&l▏ &fProchain rayon: &e" + (radius + 2) + "x" + (radius + 2),
                                "&f",
                                "&fPrix: &a" + data.getInt("radiusprice") + " grains"
                        ))).build()).onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (grain < data.getInt("radiusprice")) {
                            Lang.send(player, "harvester.grains.dont-have");
                            return;
                        }
                        data.setDouble("grains", grain - data.getInt("radiusprice"));
                        int price = 100;
                        switch (radius) {
                            case 1:
                                price = ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.RADIUS.LEVEL-2");
                                data.setInt("radius", -1);
                                ctx.getData().setInt("place_radius", 3);
                                break;
                            case 3:
                                price = ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.RADIUS.LEVEL-3");
                                data.setInt("radius", -2);
                                ctx.getData().setInt("place_radius", 5);
                                break;
                            case 5:
                                data.setInt("radius", -3);
                                ctx.getData().setInt("place_radius", 7);
                                break;
                        }
                        data.setInt("radiusprice", price);
                        ctx.updateHand();
                        player.closeInventory();
                        Lang.send(player, "harvester.grains.success.radius", "radius", String.valueOf(radius + 2));
                    }
                });
        this.setItem(11, hasteitem);
        this.setItem(13, radiusitem);
        this.setItem(15, keyfinderitem);
        this.setItem(21, spfinderitem);
        this.setItem(23, speeditem);
        this.setItem(31, autosellitem);
        this.setItem(40, new CloseItem());

    }
}
