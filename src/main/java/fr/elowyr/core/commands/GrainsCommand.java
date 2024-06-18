package fr.elowyr.core.commands;

import fr.elowyr.core.items.ItemsManager;
import fr.elowyr.core.items.data.Item;
import fr.elowyr.core.items.data.UsableItem;
import fr.elowyr.core.items.data.nbt.NBTData;
import fr.elowyr.core.items.data.nbt.NMS;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GrainsCommand extends TCommand {
    @Override
    @Command(name = "grains", permissionNode = "elowyrcore.grains")
    public void onCommand(CommandArgs args) {

        CommandSender commandSender = args.getSender();

        String[] arg = args.getArgs();

        if (arg.length < 3) {
            commandSender.sendMessage("§c/grains add <player> <number>");
            return;
        }

        if (arg[0].equalsIgnoreCase("add")) {
            Player target = Bukkit.getPlayer(arg[1]);

            if (target == null) {
                commandSender.sendMessage("§cCe joueur n'existe pas.");
                return;
            }

            ItemStack itemStack = target.getItemInHand();

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                commandSender.sendMessage("§cCe joueur n'a pas de houe en main.");
                return;
            }

            NBTData nbtData = NMS.getNBT(itemStack);

            if (nbtData == null) {
                commandSender.sendMessage("Ce joueur ne possède pas de houe dans sa main.");
                return;
            }

            if (!nbtData.toNBT().hasKey("grains")) {
                commandSender.sendMessage("Ce joueur ne possède pas de houe dans sa main.");
                return;
            }

            try {

                int number = Integer.parseInt(arg[2]);

                nbtData.addDouble("grains", number);

                commandSender.sendMessage("§aTu viens d'ajouter " + number + " grains sur la houe de " + target.getName() + ".");
                target.sendMessage("§aTa houe vient de gagner " + number + " grains !");

                target.setItemInHand(ItemsManager.getInstance().getByName("harvester")
                        .build(nbtData, 1, target.getItemInHand().getDurability()));

            } catch (NumberFormatException e) {
                commandSender.sendMessage("§cLe nombre renseigné n'est pas valide.");
            }
        }
    }
}
