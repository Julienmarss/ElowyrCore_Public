package fr.elowyr.core.ap.commands;

import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.ap.APManager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.utils.commands.annotations.Command;
import org.bukkit.entity.Player;

public class ApsInfoCommand extends TCommand {

    private final APManager manager = APManager.get();

    @Override
    @Command(name = {"ap.info", "aps.info"}, permissionNode = "elowyrcore.ap.info")
    public void onCommand(CommandArgs args) {
        final Player player = args.getPlayer();
        this.manager.sendInformation(player);
    }
}
