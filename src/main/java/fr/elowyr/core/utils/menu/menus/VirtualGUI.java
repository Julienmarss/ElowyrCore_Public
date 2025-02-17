package fr.elowyr.core.utils.menu.menus;

import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.menu.CloseAction;
import fr.elowyr.core.utils.menu.items.UpdaterItem;
import fr.elowyr.core.utils.menu.items.VirtualItem;
import fr.elowyr.core.utils.menu.items.type.BackItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VirtualGUI implements GUI {
	private String name;
	private Size size;
	private VirtualItem[] items;
	private CloseAction closeAction;
	private boolean allowBackItem;
	private boolean allowClick;

	public VirtualGUI(String name, Size size) {
		this.name = Utils.color(name);
		this.size = size;
		items = new VirtualItem[size.getSize()];
	}

	public VirtualGUI allowBackItem() {
		allowBackItem = true;
		return this;
	}

	public VirtualGUI allowClick() {
		allowClick = true;
		return this;
	}

	public VirtualGUI onCloseAction(CloseAction closeAction) {
		this.closeAction = closeAction;
		return this;
	}

	public VirtualGUI setItem(int position, VirtualItem menuItem) {
		items[position] = menuItem;
		return this;
	}

	public VirtualGUI addItem(VirtualItem item) {
		int next = getNextEmptySlot();
		if (next == -1)
			return this;
		items[next] = item;
		return this;
	}

	private int getNextEmptySlot() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public VirtualGUI fillEmptySlots(VirtualItem menuItem) {
		for (int i = 0; i < items.length; i++) {
			if ((!allowBackItem) || (i != size.getSize() - 1)) {

				if (items[i] == null)
					items[i] = menuItem;
			}
		}
		return this;
	}

	public void getBorders(VirtualGUI inv, ItemStack itemStack, int[] slots) {
		for (int i : slots)
			inv.setItem(i, new VirtualItem(itemStack));
	}

	public void open(Player player) {
		Inventory inventory = Bukkit.createInventory(new VirtualHolder(this, Bukkit.createInventory(player, size.getSize())), size.getSize(), name);
		this.apply(inventory, player);
		player.openInventory(inventory);
	}

	public void apply(Inventory inventory, Player player) {
		if ((allowBackItem) && (items[(inventory.getSize() - 1)] == null)) {
			items[(inventory.getSize() - 1)] = new BackItem();
		}

		for (int i = 0; i < items.length; i++) {
			VirtualItem item = items[i];
			if (item != null) {
				inventory.setItem(i, item.getItem());
				if ((item instanceof UpdaterItem)) {
					UpdaterItem updater = (UpdaterItem) item;
					updater.startUpdate(player, i);
				}
			}
		}
	}

	public void onInventoryClick(InventoryClickEvent event) {
		int slot = event.getRawSlot();
		if (!allowClick) {
			event.setCancelled(true);
		}
		if ((slot >= 0) && (slot < size.getSize()) && (items[slot] != null)) {
			VirtualItem item = items[slot];
			if (item.getAction() != null) {
				item.getAction().onClick(event);
			}
		}
	}

	public void onInventoryClose(InventoryCloseEvent event) {
		if (closeAction != null) {
			closeAction.onClose(event);
			closeAction = null;
		}

		for (int i = 0; i < items.length; i++) {
			VirtualItem item = items[i];
			if ((item != null) && ((item instanceof UpdaterItem))) {
				UpdaterItem updater = (UpdaterItem) item;
				updater.cancelUpdate();
			}
		}
	}

	public void destroy() {
		name = null;
		size = null;
		items = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public VirtualItem[] getItems() {
		return items;
	}

	public void setItems(VirtualItem[] items) {
		this.items = items;
	}

	public CloseAction getCloseAction() {
		return closeAction;
	}

	public void setCloseAction(CloseAction closeAction) {
		this.closeAction = closeAction;
	}

	public boolean isAllowBackItem() {
		return allowBackItem;
	}

	public void setAllowBackItem(boolean allowBackItem) {
		this.allowBackItem = allowBackItem;
	}

	public boolean isAllowClick() {
		return allowClick;
	}

	public void setAllowClick(boolean allowClick) {
		this.allowClick = allowClick;
	}
}
