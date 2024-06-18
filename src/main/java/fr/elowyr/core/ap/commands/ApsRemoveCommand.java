package fr.elowyr.core.ap.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.ap.APManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.Player;

public class ApsRemoveCommand extends TCommand {

    private final APManager manager = APManager.get();

    @Override
    @Command(name = {"ap.remove", "aps.remove", "ap.remove", "aps.remove"}, permissionNode = "elowyrcore.ap.remove")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();
        this.manager.deleteAp(player);
    }
}
