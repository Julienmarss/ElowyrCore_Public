package fr.elowyr.core.utils.menu.items.type;

import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.menu.ClickAction;
import fr.elowyr.core.utils.menu.MenuManager;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import fr.elowyr.core.utils.menu.menus.VirtualGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BackItem extends VirtualItem {

	public BackItem() {
		super(ItemBuilder.newBuilder(Material.ARROW, 0).displayName(Utils.color("&c&lRetour")).build());

		this.onItemClick(new ClickAction() {
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				VirtualGUI gui = (VirtualGUI) MenuManager.getInstance().getGuis().get(player.getUniqueId());
				if (gui != null) {
					gui.open(player);
				}
			}
		});
	}
}
