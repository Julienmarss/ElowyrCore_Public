package fr.elowyr.core.tasks;

import fr.elowyr.core.utils.BukkitUtils;
import fr.elowyr.core.utils.Utils;
import fr.elowyr.core.data.IEvent;
import fr.elowyr.core.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Locale;

public class PreStartTask extends BukkitRunnable {
    private final IEvent tEvent;
    private String name;
    private Object location;
    private final int interval;
    private final String[] lines;

    private int time = 5; //MAJ remettre le temps
    private int index;

    public PreStartTask(IEvent event, String name, Object location) {
        this.tEvent = event;
        this.name = name;
        this.location = location;
        List<String> linesList = (List<String>) Lang.get().get("event." + event.getName().toLowerCase(Locale.ROOT) + ".title");
        this.lines = linesList.toArray(new String[0]);
        this.interval = (60 / lines.length) - 5;
    }

    @Override
    public void run() {
        String message = name == "fdp" ? "§f§l» §fLe §e" + tEvent.getName() + " §fva débuter dans §b" + time + " §fsecondes." : "§f§l» §fLe §e" + tEvent.getName() + " " + name + " §fva débuter dans §b" + time + " §fsecondes.";
        if (time == 60 || time == 30 || time == 15 || time == 10 || time == 5) {
            Bukkit.broadcastMessage(message);
        }

        if (lines.length > index && (60 - ((index + 1) * interval)) == time) {
            Bukkit.getOnlinePlayers().forEach(player -> BukkitUtils.sendTitle(player, Utils.color("&6&lElowyr"), lines[index], 20, 40, 20));
            index++;
        }

        if (time == 0) {
            this.tEvent.run(name);
            cancel();
        }

        time--;
    }

    public IEvent gettEvent() {
        return tEvent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public int getInterval() {
        return interval;
    }

    public String[] getLines() {
        return lines;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
