package fr.elowyr.core.commands;

import fr.elowyr.core.managers.Manager;
import fr.elowyr.core.utils.commands.CommandArgs;
import fr.elowyr.core.commands.TCommand;
import fr.elowyr.core.utils.commands.annotations.Command;

public class ElowyrReloadCommand extends TCommand {

    @Command(name = "elowyrcore.reload", permissionNode = "elowyrcore.admin")
    public void onCommand(CommandArgs args) {
        Manager.reload();
    }
}
