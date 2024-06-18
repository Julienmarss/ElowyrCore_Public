package fr.elowyr.core.utils.menu.items.type;

import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.menu.ClickAction;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CloseItem extends VirtualItem {

	public CloseItem() {
		super(ItemBuilder.newBuilder(Material.INK_SACK, 1).displayName(Utils.color("&c&lFermer")).build());

		this.onItemClick(new ClickAction() {
			public void onClick(InventoryClickEvent event) {
				event.getWhoClicked().closeInventory();
			}
		});
	}
}
