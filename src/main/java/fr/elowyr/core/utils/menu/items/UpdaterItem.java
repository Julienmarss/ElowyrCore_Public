package fr.elowyr.core.utils.menu.items;

import fr.elowyr.core.utils.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpdaterItem extends VirtualItem {
	private int delay;
	private UpdateAction updateAction;
	private int TASK_ID = -1;

	public UpdaterItem(ItemStack item, int delay) {
		super(item);

		this.delay = delay;
	}

	public UpdaterItem setUpdateAction(UpdateAction updateAction) {
		this.updateAction = updateAction;
		return this;
	}

	public void startUpdate(final Player player, final int slot) {
		if (updateAction == null) {
			return;
		}
		TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MenuManager.getInstance().getPlugin(),
				new Runnable() {
					public void run() {
						Inventory inv = player.getOpenInventory().getTopInventory();
						inv.setItem(slot, updateAction.action(player, getItem()));
					}
				}, 1L, delay);
	}

	public void cancelUpdate() {
		Bukkit.getScheduler().cancelTask(TASK_ID);
	}

	public static abstract class UpdateAction {
		public abstract ItemStack action(Player paramPlayer, ItemStack paramItemStack);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public UpdateAction getUpdateAction() {
		return updateAction;
	}

	public int getTASK_ID() {
		return TASK_ID;
	}

	public void setTASK_ID(int TASK_ID) {
		this.TASK_ID = TASK_ID;
	}
}
