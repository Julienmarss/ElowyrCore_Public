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

public class HarvesterGUI extends VirtualGUI {

    public HarvesterGUI(NBTData data, Player player, ItemContext<?> ctx) {
        super(Lang.get().getString("harvester.gui.name"), Size.TROIS_LIGNE);

        double grain = data.getDouble("grains");

        int[] slots = new int[] {0, 1, 7, 8, 9, 17, 18, 19, 25, 26};

        this.getBorders(this, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE, 1).displayName("§f").build(), slots);

        this.setItem(12, new VirtualItem(ItemBuilder.newBuilder(Material.PAPER, 0).displayName(Utils.color("&6Choix de Cultures"))
                .lore(Utils.colorAll(Arrays.asList(
                        "",
                        "&7▪ &fCela te permet de &echoisir&f la",
                        " &fculture que tu désires &aplanter&f."
                        )
                ))).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                HarvesterChoseGUI gui = new HarvesterChoseGUI(data, player, ctx);
                gui.open(player);
            }
        }));

        this.setItem(14, new VirtualItem(ItemBuilder.newBuilder(Material.BOOK, 0).displayName(Utils.color("&6Améliorations"))
                .lore(Utils.colorAll(Arrays.asList(
                        "",
                        "&7▪ &aAchète&f une multitude",
                        " &fd'&eaméliorations&f pour ta houe."
                )))).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                HarvesterUpgradeGUI gui = new HarvesterUpgradeGUI(data, player, ctx);
                gui.open(player);
            }
        }));
    }
}
