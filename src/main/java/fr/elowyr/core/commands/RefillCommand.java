package fr.elowyr.core.commands;

import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.utils.VaultUtils;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RefillCommand extends TCommand {


    @Override
    @Command(name = "refill", permissionNode = "elowyrcore.refill")
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        int quantitySlot = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null) {
                quantitySlot++;
            }
        }
        double money = 0;
        ItemStack potion = new ItemStack(Material.POTION, 1, (short) 16421);
        double price = ShopGuiPlusApi.getItemStackPriceBuy(potion);
        if (price > 0.0) {
            money = price * quantitySlot;

            if (quantitySlot <= 0) {
                player.sendMessage(Utils.color("&6&lRefill &7◆ &fVous &cn'avez &fpas de place dans votre&e inventaire&f."));
                return;
            }

            if (!VaultUtils.has(player, money)) {
                player.sendMessage(Utils.color("&6&lRefill &7◆ &fVous &cn'avez &fpas &el'argent &fnécessaire."));
                return;
            }

            for (int i = 0; i < quantitySlot; i++) {
                player.getInventory().addItem(potion);
            }
        }
        VaultUtils.withdrawMoney(player, money);
        player.sendMessage(Utils.color("&6&lRefill &7◆ &fVous avez été &drefill&f pour &e" + money +  "$&f ! &7(" + quantitySlot + ")"));
    }
}
