package fr.elowyr.core.utils.menu.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class VirtualHolder implements InventoryHolder {

  private GUI gui;
  private Inventory inventory;
  private int nextPage;
  
  public VirtualHolder(GUI gui, Inventory inventory) {
    this.gui = gui;
    this.inventory = inventory;
    this.nextPage = -1;
  }

  public GUI getGui() {
    return gui;
  }

  public void setGui(GUI gui) {
    this.gui = gui;
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public int getNextPage() {
    return nextPage;
  }

  public void setNextPage(int nextPage) {
    this.nextPage = nextPage;
  }
}
