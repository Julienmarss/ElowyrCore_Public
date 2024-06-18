package fr.elowyr.core.utils.menu.items;

import fr.elowyr.core.utils.ItemBuilder;
import fr.elowyr.core.utils.menu.ClickAction;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class VirtualItem {
	
  private final ItemStack item;
  private ClickAction action;
  private ClickType type;
  
  public VirtualItem(ItemStack item) {
    this.item = item;
    action = null;
  }
  public VirtualItem(ItemBuilder item) {
      this.item = item.build();
      this.action = null;
  }

  public VirtualItem onItemClick(ClickAction action) {
    this.action = action;
    return this;
  }

  public VirtualItem onRightItemClick(ClickAction action) {
    this.action = action;
    this.type = ClickType.RIGHT;
    return this;
  }

  public ItemStack getItem() {
    return item;
  }

  public ClickAction getAction() {
    return action;
  }

  public void setAction(ClickAction action) {
    this.action = action;
  }

  public ClickType getType() {
    return type;
  }

  public void setType(ClickType type) {
    this.type = type;
  }
}
