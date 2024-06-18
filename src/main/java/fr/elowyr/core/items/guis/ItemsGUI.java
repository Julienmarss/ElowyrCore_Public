package fr.elowyr.core.items.guis;

import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.utils.InventoryUtils;
import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.menu.BoardContainer;
import fr.elowyr.core.utils.menu.ClickAction;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import fr.elowyr.core.utils.menu.menus.PaginationGUI;
import fr.elowyr.core.utils.menu.menus.VirtualGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemsGUI extends PaginationGUI {

    public ItemsGUI(int page, Player player, ItemsManager itemsManager) {
        super(page, itemsManager.getALL().size(), 28, "§6§lElowyr §7▸ §eItems");
        this.setMaxItemsPerPage(28);

        getBorders(this, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9));

        if (getPage() > 1) {
            this.setItem(47, new VirtualItem(new ItemBuilder(new ItemStack(Material.ARROW)).displayName("§7§l→ §fPage §aPrécédente")).onItemClick(new ClickAction() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    new ItemsGUI(1, player, itemsManager).open((Player) event.getWhoClicked());
                }
            }));
        }
        if (getPage() < getTotalPages()) {
            this.setItem(51, new VirtualItem(new ItemBuilder(new ItemStack(Material.ARROW)).displayName("§7§l→ §fPage §aSuivante")).onItemClick(new ClickAction() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    new ItemsGUI(page + 1, player, itemsManager).open((Player) event.getWhoClicked());
                }
            }));
        }

        List<UsableItem> items = ItemsManager.getInstance().ALL;

        int[] datas = this.getDatas();
        final BoardContainer boardContainer = this.getBoardContainer(10, 7, 4);
        for (int count = datas[0], index = 0; count < datas[1] && index < boardContainer.getSlots().size(); index++, count++) {
            UsableItem data = items.get(count);
            this.setItem(boardContainer.getSlots().get(index), new VirtualItem(new ItemBuilder(data.build(player.getName()))).onItemClick(new ClickAction() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (event.isLeftClick()) {
                        InventoryUtils.addItem(player, data.build(player.getName()), 1);
                        player.sendMessage(Utils.color("&fVous venez de &6recevoir &a" + data.getName() + " &f!"));
                    } else if (event.isRightClick()) {
                        data.setActive(!data.isActive());
                        player.sendMessage(Utils.color("&fVous venez " + (data.isActive() ? "&ad'activer" : "&fde &cdésactiver") + "&f l'item &b" + data.getName() + "&f !"));
                    }
                    player.closeInventory();
                }
            }));
            this.open(player);
        }
    }

    public void getBorders(VirtualGUI inv, ItemStack itemStack) {
        for (int i : new int[]{ 0, 1, 7, 8, 45, 46, 52, 53 })
            inv.setItem(i, new VirtualItem(itemStack));
    }
}
