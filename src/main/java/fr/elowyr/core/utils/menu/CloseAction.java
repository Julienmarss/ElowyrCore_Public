package fr.elowyr.core.utils.menu;

import org.bukkit.event.inventory.InventoryCloseEvent;

public abstract class CloseAction {
	
  public CloseAction() {}
  public abstract void onClose(InventoryCloseEvent paramInventoryCloseEvent);
}
