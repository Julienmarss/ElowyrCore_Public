package fr.elowyr.core.ap.guis;

import fr.elowyr.core.ap.APManager;
import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.VaultUtils;
import fr.elowyr.core.utils.menu.ClickAction;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import fr.elowyr.core.utils.menu.menus.Size;
import fr.elowyr.core.utils.menu.menus.VirtualGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ApBuyConfirmGUI extends VirtualGUI {

    public ApBuyConfirmGUI(APManager manager, final long price) {

        super("§eConfirmation", Size.CINQ_LIGNE);

        this.getBorders(this, ItemBuilder.newBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1)).displayName("§f").build(), new int[]{0, 1, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 43, 44});

        this.setItem(21, new VirtualItem(ItemBuilder.newBuilder(Material.INK_SACK, 10)
                .displayName(Utils.color("&f• &aConfirmer &f•"))
                .lore(Utils.colorAll(Arrays.asList("&f", "&7◆ &fPrix: &e" + VaultUtils.getEconomy().format(price), "&f")))
                .build())
                .onItemClick(new ClickAction() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        manager.buyAp((Player) event.getWhoClicked());
                        event.getWhoClicked().closeInventory();
                    }
                }));

        this.setItem(23, new VirtualItem(ItemBuilder.newBuilder(Material.INK_SACK, 1).displayName(Utils.color("&f• &cRefuser &f•")).build()).onItemClick(new ClickAction() {
            @Override
            public void onClick(InventoryClickEvent event) {
                event.getWhoClicked().closeInventory();
            }
        }));
    }
}
