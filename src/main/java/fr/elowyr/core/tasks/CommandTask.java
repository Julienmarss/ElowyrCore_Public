package fr.elowyr.core.tasks;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class CommandTask implements Runnable {

    private final List<String> commands;
    
    public CommandTask(final List<String> commands) {
        this.commands = commands;
    }
    
    @Override
    public void run() {
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        for (final String command : this.commands) {
            Bukkit.dispatchCommand(console, command);
        }
    }
}
