package fr.elowyr.core.utils.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class ClickAction {
	
  public ClickAction() {}
  public abstract void onClick(InventoryClickEvent event);
}
