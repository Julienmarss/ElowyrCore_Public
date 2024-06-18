package fr.elowyr.core.items.commands;

import fr.elowyr.core.items.data.Item;
import fr.elowyr.core.lang.Lang;
import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.utils.InventoryUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ItemsGiveCommand extends TCommand {

    @Override
    @Command(name = "items.give", permissionNode = "elowyrcore.items")
    public void onCommand(CommandArgs args) {
        if (args.length() < 3) {
            Lang.send(args.getSender(), "items.give.usage");
            return;
        }
        final Player player = Bukkit.getPlayer(args.getArgs(0));
        if (player == null || !player.isOnline()) {
            Lang.send(args.getSender(), "items.give.player-not-found", "name", args.getArgs(0));
            return;
        }
        final String itemName = args.getArgs(1);
        final Item item = ItemsManager.getInstance().getByName(itemName);
        final Item armorhelmet = ItemsManager.getInstance().getByName("farm_helmet");
        final Item armorchestplate = ItemsManager.getInstance().getByName("farm_chestplate");
        final Item armorleggings = ItemsManager.getInstance().getByName("farm_leggings");
        final Item armorboots = ItemsManager.getInstance().getByName("farm_boots");
        if (itemName.equalsIgnoreCase("armor")) {
            InventoryUtils.addItem(player, armorhelmet.build(), 1);
            InventoryUtils.addItem(player, armorchestplate.build(), 1);
            InventoryUtils.addItem(player, armorleggings.build(), 1);
            InventoryUtils.addItem(player, armorboots.build(), 1);
            return;
        }
        if (item == null) {
            Lang.send(args.getSender(), "items.give.no-item", "name", itemName);
            return;
        }
        if (item.isInvalid()) {
            Lang.send(args.getSender(), "items.give.invalid-item", "name", itemName);
            return;
        }
        final int amount = Integer.parseInt(args.getArgs(2));
        if (amount < 1) {
            Lang.send(args.getSender(), "items.give.invalide-amount");
            return;
        }
        InventoryUtils.addItem(player, item.build(player.getName()), amount);
        player.updateInventory();
        Lang.send(args.getSender(), "items.give.success", "player", player.getName(), "item", item.getName(), "amount", amount);
    }
}
