package fr.elowyr.core.commands;

import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.config.Config;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class FurnaceCommand extends TCommand {

    @Command(name = "furnace", permissionNode = "elowyrcore.furnace")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        int count = 0;
        for (final Map.Entry<Material, Material> entry : Config.get().getFurnaceItems().entrySet()) {
            final int amount = inventory.all(entry.getKey()).values().stream().mapToInt(ItemStack::getAmount).sum();
            if (amount > 0) {
                inventory.remove(entry.getKey());
                inventory.addItem(new ItemStack(entry.getValue(), amount));
                count += amount;
            }
        }
        if (count > 0) {
            player.updateInventory();
            Lang.send(player, "furnace.success", "amount", count);
        }
        else {
            Lang.send(player, "furnace.no-item");
        }
    }
}
