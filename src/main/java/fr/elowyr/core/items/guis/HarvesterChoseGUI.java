package fr.elowyr.core.items.guis;

import fr.elowyr.core.items.data.ItemContext;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.menu.ClickAction;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import fr.elowyr.core.utils.menu.menus.Size;
import fr.elowyr.core.utils.menu.menus.VirtualGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class HarvesterChoseGUI extends VirtualGUI {

    public HarvesterChoseGUI(NBTData data, Player player, ItemContext<?> ctx) {
        super(Lang.get().getString("harvester.gui.name"), Size.TROIS_LIGNE);

        double grain = data.getDouble("grains");

        int[] slots = new int[] {0, 1, 7, 8, 9, 17, 18, 19, 25, 26};

        this.getBorders(this, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE, 1).displayName("§f").build(), slots);

        this.setItem(4, new VirtualItem(ItemBuilder.newBuilder(Material.SKULL_ITEM, 3).owner(player.getName())
                .displayName(Utils.color("&eInformations &7▸ &6" + player.getName()))
                .lore(Utils.colorAll(Arrays.asList(
                        "&f",
                        "&f⋄ &7Pseudo: &f" + player.getName(),
                        "&f⋄ &7Grains: &f" + String.format("%.2f",grain)
                        ))).build()));

        this.setItem(11, new VirtualItem(ItemBuilder.newBuilder(Material.CARROT_ITEM, 0)).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                data.setString("culture", "CARROT");
                ctx.updateHand();
                player.sendMessage(Utils.color("&fVous avez &aactivé&f la culture &e" + data.getString("culture")));
            }
        }));
        this.setItem(12, new VirtualItem(ItemBuilder.newBuilder(Material.POTATO_ITEM, 0)).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                data.setString("culture", "POTATO");
                ctx.updateHand();
                player.sendMessage(Utils.color("&fVous avez &aactivé&f la culture &e" + data.getString("culture")));
            }
        }));

        this.setItem(14, new VirtualItem(ItemBuilder.newBuilder(Material.WHEAT, 0)).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                data.setString("culture", "SEEDS");
                ctx.updateHand();
                player.sendMessage(Utils.color("&fVous avez &aactivé&f la culture &e" + data.getString("culture")));
            }
        }));
        this.setItem(15, new VirtualItem(ItemBuilder.newBuilder(Material.NETHER_STALK, 0)).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                data.setString("culture", "NETHER_WARTS");
                ctx.updateHand();
                player.sendMessage(Utils.color("&fVous avez &aactivé&f la culture &e" + data.getString("culture")));
            }
        }));
    }
}
