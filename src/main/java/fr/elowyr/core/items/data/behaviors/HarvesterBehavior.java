package fr.elowyr.core.items.data.behaviors;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayers;
import fr.elowyr.basics.factions.FactionManager;
import fr.elowyr.basics.factions.data.FactionData;
import fr.elowyr.basics.missions.MissionManager;
import fr.elowyr.basics.missions.data.MissionType;
import fr.elowyr.basics.upgrade.UpgradeManager;
import fr.elowyr.basics.upgrade.data.Upgrade;
import fr.elowyr.basics.upgrade.data.UpgradeType;
import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.items.guis.HarvesterGUI;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.InventoryUtils;
import fr.elowyr.core.utils.ShopGuiPlusHook;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.events.AbstractEvent;
import fr.elowyr.events.ElowyrEvents;
import fr.elowyr.events.farm.Farm;
import fr.elowyr.events.farm.FarmManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class HarvesterBehavior extends BasicBehavior {

    private int multiplier;

    public HarvesterBehavior() {
        super(UseType.BLOCK_BREAK, UseType.RIGHT_CLICK);
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
    }

    @Override
    public void getDefaultData(NBTData data) {
        data.setInt("grains", 0);
        data.setInt("block_broken", 0);
        data.setInt("radius", 0);
        data.setInt("place_radius", 1);
        data.setString("place_autosell", "§c✘");
        data.setString("culture", "NETHER_WARTS");
        data.setInt("radiusprice", ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.RADIUS.LEVEL-1"));
        data.setBoolean("autosell", false);
        data.setInt("autosellprice", ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.AUTOSELL"));
        data.setInt("speed", 0);
        data.setInt("speedprice", ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.SPEED.LEVEL-1"));
        data.setBoolean("haste", false);
        data.setInt("hasteprice", ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.HASTE"));
        data.setDouble("key_finder", 0);
        data.setDouble("key_finderprice", ElowyrCore.getInstance().getConfig().getInt("HOUE.PRICE.KEYFINDER.LEVEL-1"));
        data.setDouble("spawner_finder", 0);
        data.setDouble("spawner_finderprice", ElowyrCore.getInstance().getConfig().getDouble("HOUE.PRICE.SPAWNERFINDER.LEVEL-1"));
    }

    @Override
    public boolean use(ItemContext<?> context) {
        if (context.getUseType().equals(UseType.BLOCK_BREAK)) {
            this.blockBreak(context);
        } else {
            PlayerInteractEvent event = (PlayerInteractEvent) context.getEvent();
            final Player player = event.getPlayer();
            if (!event.getPlayer().isSneaking()) {
                final ItemStack stack = player.getItemInHand();
                if (stack == null || event.getClickedBlock() == null) return false;

                if (event.getClickedBlock().getType() == Material.SOUL_SAND || event.getClickedBlock().getType() == Material.SOIL) {

                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;

                    int plantedCount = 0;

                    int radius = context.getData().getInt("radius");

                    for (int x = radius; x <= Math.abs(radius); x++) {
                        for (int z = radius; z <= Math.abs(radius); z++) {
                            final Block radiusBlock = event.getClickedBlock().getLocation().clone().add(x, 0.0, z).getBlock();
                            if (radiusBlock != null) {
                                if (radiusBlock.getType() != Material.AIR) {
                                    Block aboveBlock = event.getClickedBlock().getLocation().clone().add(x, 1.0, z).getBlock();
                                    if (context.getData().getString("culture").equalsIgnoreCase("NETHER_WARTS")) {
                                        if (radiusBlock.getType() == Material.SOUL_SAND) {
                                            if (!player.getInventory().contains(Material.NETHER_STALK)) {
                                                player.sendMessage(Utils.color("&cVous n'avez pas de culture"));
                                                return false;
                                            }
                                            if (aboveBlock.getType() != Material.NETHER_WARTS) {
                                                aboveBlock.setType(Material.NETHER_WARTS);
                                                plantedCount++;
                                            }
                                        }
                                    } else if (context.getData().getString("culture").equalsIgnoreCase("CARROT")) {
                                        if (radiusBlock.getType() == Material.SOIL) {
                                            if (!player.getInventory().contains(Material.CARROT_ITEM)) {
                                                player.sendMessage(Utils.color("&cVous n'avez pas de culture"));
                                                return false;
                                            }
                                            if (aboveBlock.getType() != Material.POTATO && aboveBlock.getData() < 1
                                                    || aboveBlock.getType() != Material.CROPS && aboveBlock.getData() < 1
                                                    || aboveBlock.getType() != Material.CARROT && aboveBlock.getData() < 1) {
                                                aboveBlock.setType(Material.CARROT);
                                                plantedCount++;
                                            }
                                        }
                                    } else if (context.getData().getString("culture").equalsIgnoreCase("POTATO")) {
                                        if (radiusBlock.getType() == Material.SOIL) {
                                            if (!player.getInventory().contains(Material.POTATO_ITEM)) {
                                                player.sendMessage(Utils.color("&6&lEmpereur &7◆ &fVous &cn'avez &fpas de &eculture&f. &7(" + context.getData().getString("culture").toLowerCase()) + ")");
                                                return false;
                                            }
                                            if (aboveBlock.getType() != Material.POTATO && aboveBlock.getData() < 1
                                                    || aboveBlock.getType() != Material.CROPS && aboveBlock.getData() < 1
                                                    || aboveBlock.getType() != Material.CARROT && aboveBlock.getData() < 1) {
                                                aboveBlock.setType(Material.POTATO);
                                                plantedCount++;
                                            }
                                        }
                                    } else if (context.getData().getString("culture").equalsIgnoreCase("SEEDS")) {
                                        if (radiusBlock.getType() == Material.SOIL) {
                                            if (!player.getInventory().contains(Material.SEEDS)) {
                                                player.sendMessage(Utils.color("&6&lEmpereur &7◆ &fVous &cn'avez &fpas de &eculture&f. &7(" + context.getData().getString("culture").toLowerCase()) + ")");
                                                return false;
                                            }
                                            if (aboveBlock.getType() != Material.POTATO && aboveBlock.getData() < 1
                                                    || aboveBlock.getType() != Material.CROPS && aboveBlock.getData() < 1
                                                    || aboveBlock.getType() != Material.CARROT && aboveBlock.getData() < 1) {
                                                aboveBlock.setType(Material.CROPS);
                                                plantedCount++;
                                            }
                                        }
                                    }
                                    MissionManager.getInstance().updateMission(MissionType.BLOCK_PLACE, player, aboveBlock.getType(), plantedCount, true);
                                    Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(aboveBlock, ActionType.PLACE), aboveBlock);
                                    FactionData factionData = FactionManager.getInstance().getProvider().get(FPlayers.getInstance().getByPlayer(player).getFactionId());
                                    if (factionData == null) return false;

                                    List<Upgrade> upgrades = UpgradeManager.getInstance().getUpgradeByTarget(factionData, aboveBlock.getType().name());
                                    if (upgrades == null) return false;

                                    upgrades.forEach(upgrade -> {
                                        if (upgrade == null) return;

                                        if (upgrade.getUpgradeType() != UpgradeType.PLACE) return;
                                        if (upgrade.getActualGoal() >= upgrade.getGoal()) return;

                                        upgrade.setActualGoal(upgrade.getActualGoal() + 1);
                                    });
                                }
                            }
                        }
                    }

                    if (plantedCount > 0) {
                        if (context.getData().getString("culture").equalsIgnoreCase("SEEDS")) {
                            player.getInventory().removeItem(new ItemStack(Material.SEEDS, plantedCount));
                        } else if (context.getData().getString("culture").equalsIgnoreCase("POTATO")) {
                            player.getInventory().removeItem(new ItemStack(Material.POTATO_ITEM, plantedCount));
                        } else if (context.getData().getString("culture").equalsIgnoreCase("CARROT")) {
                            player.getInventory().removeItem(new ItemStack(Material.CARROT_ITEM, plantedCount));
                        } else if (context.getData().getString("culture").equalsIgnoreCase("NETHER_WARTS")) {
                            player.getInventory().removeItem(new ItemStack(Material.NETHER_STALK, plantedCount));
                        }
                    }
                }
            } else {
                final HarvesterGUI gui = new HarvesterGUI(context.getData(), player, context);
                gui.open(player);
            }
        }
        return false;
    }

    private void blockBreak(final ItemContext<?> ctx) {
        final BlockBreakEvent event = (BlockBreakEvent) ctx.getEvent();
        if (event.isCancelled()) {
            return;
        }
        Player player = ctx.getPlayer();
        Block block = event.getBlock();
        int grains = 0;
        int radius = ctx.getData().getInt("radius");
        final List<Block> blocks = Lists.newArrayList();
        List<ItemStack> drops = Lists.newArrayList();
        event.setCancelled(true);
        for (int x = radius; x <= Math.abs(radius); x++) {
            for (int z = radius; z <= Math.abs(radius); z++) {
                Location add = event.getBlock().getLocation().clone().add(x, 0, z);
                Block radiusBlock = add.getBlock();
                if (radiusBlock == block) continue;
                for (FarmType farmType : FarmType.values()) {
                    if (farmType.getMaterial() == radiusBlock.getType()) {
                        if (radiusBlock.getData() < farmType.getData()) {
                            break;
                        }
                        if (!BukkitUtils.canBreak(radiusBlock, player, FPlayers.getInstance().getByPlayer(player))) {
                            player.sendMessage(Utils.color("&6&lEmpereur &7◆ &fVous devez être sur vos &cterritoires&f pour &autiliser &fla houe !"));
                            return;
                        }
                        if (radiusBlock.getData() == farmType.getData()) {
                            blocks.add(radiusBlock);
                            grains++;
                            if (radiusBlock.getType() == Material.CROPS) {
                                drops.add(new ItemStack(farmType.getDrops(), 1));
                            } else {
                                drops.add(new ItemStack(farmType.getDrops(), Utils.randomInt(1, 3)));
                            }

                            Jobs.action(Jobs.getPlayerManager().getJobsPlayer(player), new BlockActionInfo(block, ActionType.BREAK), block);
                            radiusBlock.setData((byte) 0);

                            if (player.getInventory().firstEmpty() == -1) {
                                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), new ItemStack(farmType.getDrops()));
                            } else {
                                InventoryUtils.addItems(player, drops);
                            }

                            MissionManager.getInstance().updateMission(MissionType.BREAK_BLOCK, player, radiusBlock.getType(), 1, false);
                            FactionData factionData = FactionManager.getInstance().getProvider().get(FPlayers.getInstance().getByPlayer(player).getFactionId());
                            if (factionData == null) return;

                            List<Upgrade> upgrades = UpgradeManager.getInstance().getUpgradeByTarget(factionData, block.getType().name());
                            if (upgrades == null) return;

                            upgrades.forEach(upgrade -> {
                                if (upgrade == null) return;

                                if (upgrade.getUpgradeType() != UpgradeType.BREAK) return;
                                if (upgrade.getActualGoal() >= upgrade.getGoal()) return;
                                upgrade.setActualGoal(upgrade.getActualGoal() + 1);
                            });
                            BukkitUtils.sendActionBar(player, Utils.color("&2&l✔ &7◆ &fVous avez &arécupéré &e" + (ctx.getData().getInt("place_radius") * ctx.getData().getInt("place_radius")) + " " + radiusBlock.getType() + " &fpour votre &6faction&f."));
                        }
                    }
                }
            }
        }
        AbstractEvent farm = ElowyrEvents.getInstance().getEvents().getEvent("Farm");
        final FarmManager manager = ((Farm) farm).farmManager;
        if (farm.isStarted()) {
            manager.incrementPoints(FPlayers.getInstance().getByPlayer(player).getFaction(), drops.size());
        }

        if (ctx.getData().getBoolean("autosell")) {
            ShopGuiPlusHook.sellItems(player, drops, "listeners.harvested");
        }

        if (grains > 0) {
            float key = Utils.randomFloat(0, 100);
            float spawner = Utils.randomFloat(0, 100);

            if (ctx.getData().getDouble("key_finder") == 1) {
                if (key <= 0.1) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), key2[new Random().nextInt(key2.length)].replace("PLAYER", player.getName()));
                    BukkitUtils.sendActionBar(player, Lang.get().getString("harvester.actionbar.key"));
                }
            } else if (ctx.getData().getDouble("key_finder") == 2) {
                if (key <= 0.2) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), key3[new Random().nextInt(key3.length)].replace("PLAYER", player.getName()));
                    BukkitUtils.sendActionBar(player, Lang.get().getString("harvester.actionbar.key"));
                }
            } else if (ctx.getData().getDouble("spawner_finder") == 1) {

                if (spawner <= ElowyrCore.getInstance().getConfig().getDouble("HOUE.SPAWNERS.LEVEL-1")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), spawners2[new Random().nextInt(spawners2.length)]
                            .replace("PLAYER", player.getName()));
                    BukkitUtils.sendActionBar(player, Lang.get().getString("harvester.actionbar.spawner"));
                }
            } else if (ctx.getData().getDouble("spawner_finder") == 2) {

                if (spawner <= ElowyrCore.getInstance().getConfig().getDouble("HOUE.SPAWNERS.LEVEL-2")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), spawners3[new Random().nextInt(spawners3.length)]
                            .replace("PLAYER", player.getName()));

                    BukkitUtils.sendActionBar(player, Lang.get().getString("harvester.actionbar.spawner"));
                }
            }
            //Bukkit.getServer().getPluginManager().callEvent(new HoeUseEvent(player, blocks));
            ctx.getData().addInt("grains", grains);
        }
    }

    //* SPAWNERS FINDER *//
    private static final String[] spawners2 = new String[]{
            "spawners give PLAYER PIG 1",
            "spawners give PLAYER CHICKEN 1",
            "spawners give PLAYER SHEEP 1",
    };
    private static final String[] spawners3 = new String[]{
            "spawners give PLAYER PIG 1",
            "spawners give PLAYER CHICKEN 1",
            "spawners give PLAYER SHEEP 1",
            "spawners give PLAYER ZOMBIE 1",
            "spawners give PLAYER SKELETON 1",
            "spawners give PLAYER SPIDER 1",
    };

    //* KEY FINDER *//
    private static final String[] key2 = new String[]{
            "cc give p Vote 1 PLAYER",
            "cc give p Quete 1 PLAYER",
    };
    private static final String[] key3 = new String[]{
            "cc give p Vote 1 PLAYER",
            "cc give p Quete 1 PLAYER",
            "cc give p Commun 1 PLAYER",
    };
    private static final String[] key5 = new String[]{
            "cc give p Vote 1 PLAYER",
            "cc give p Quete 1 PLAYER",
            "cc give p Commun 1 PLAYER",
            "cc give p Rare 1 PLAYER",
    };
    //* KEY FINDER *//
}
