package fr.elowyr.core.utils.menu.menus;

import fr.elowyr.core.utils.menu.items.VirtualItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public interface GUI {
	
  GUI setItem(int paramInt, VirtualItem paramVirtualItem);
  GUI addItem(VirtualItem paramVirtualItem);
  void open(Player paramPlayer);
  void apply(Inventory paramInventory, Player paramPlayer);
  void onInventoryClick(InventoryClickEvent paramInventoryClickEvent);
  void onInventoryClose(InventoryCloseEvent paramInventoryCloseEvent);
}
