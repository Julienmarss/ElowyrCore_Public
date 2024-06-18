package fr.elowyr.core.utils.menu;

import org.bukkit.entity.Player;

public abstract class ConfirmAction {
	
  public ConfirmAction() {}
  public abstract void confirm(Player paramPlayer, String paramString);
}
