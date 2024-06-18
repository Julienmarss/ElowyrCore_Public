package fr.elowyr.core.scheduler;

import fr.elowyr.core.ElowyrCore;
import fr.elowyr.core.tasks.CommandTask;
import fr.elowyr.core.tasks.SyncTask;
import fr.elowyr.core.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class SchedulerManager {

    private static SchedulerManager instance;
    private final Plugin plugin;
    private final File file;
    private ScheduledExecutorService service;
    
    public SchedulerManager(final Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "scheduler.yml");
    }
    
    public void load() {
        if (!this.file.exists()) {
            this.plugin.saveResource("scheduler.yml", false);
        }
        YamlConfiguration config;
        try {
            config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Files.newInputStream(this.file.toPath()), StandardCharsets.UTF_8)));
        }
        catch (Throwable ex) {
            ElowyrCore.severe("Failed to load config");
            ex.printStackTrace();
            config = new YamlConfiguration();
        }
        final ZoneId zoneId = ZoneId.of(Objects.requireNonNull(config.getString("timezone")));
        final Stream<ConfigurationSection> tasks = ConfigUtils.getSectionList(config, "tasks");
        this.service = Executors.newScheduledThreadPool(config.getInt("pool-size", 1));
        tasks.forEach(task -> {
            List<String> commands = task.getStringList("commands");
            if (commands == null || commands.isEmpty()) {
                return;
            }
            else {
                if (task.contains("start")) {
                    this.timedTask(task, commands, zoneId);
                }
                else {
                    long delay = task.getLong("delay");
                    long period = task.getLong("period");
                    try {
                        TimeUnit unit = TimeUnit.valueOf(task.getString("unit"));
                        ScheduledExecutorService service = this.service;
                        SyncTask syncTask = new SyncTask(new CommandTask(commands));
                        service.scheduleAtFixedRate(syncTask, delay, period, unit);
                    }
                    catch (Throwable ex2) {
                        ElowyrCore.severe("Unknown TimeUnit " + task.getString("unit"));
                    }
                }
            }
        });
        ElowyrCore.info("Scheduler loaded");
    }
    
    public void timedTask(final ConfigurationSection section, final List<String> commands, final ZoneId zoneId) {
        final int hours = section.getInt("start.hours");
        final int minutes = section.getInt("start.minutes");
        final int seconds = section.getInt("start.seconds");
        final LocalTime startTime = LocalTime.of(hours, minutes, seconds);
        final List<DayOfWeek> days = ConfigUtils.getDayList(section, "start.day");
        final LocalDateTime now = LocalDateTime.now(zoneId);
        if (days != null) {
            for (final DayOfWeek day : days) {
                this.createTimedTask(now, startTime, commands, day);
            }
        }
        else {
            this.createTimedTask(now, startTime, commands, null);
        }
    }
    
    private void createTimedTask(final LocalDateTime now, final LocalTime startTime, final List<String> commands, final DayOfWeek day) {
        final LocalDateTime startDateTime = this.findNextDateTime(now, day, startTime);
        final long delay = ChronoUnit.MILLIS.between(now, startDateTime);
        final long period = TimeUnit.DAYS.toMillis((day == null) ? 1L : 7L);
        ElowyrCore.info("Starting task at " + startDateTime + " (delay: " + delay + " ms, period: " + period + " ms)");
        this.service.scheduleAtFixedRate(new SyncTask(new CommandTask(commands)), delay, period, TimeUnit.MILLISECONDS);
    }
    
    private LocalDateTime findNextDateTime(final LocalDateTime now, @Nullable final DayOfWeek day, final LocalTime startTime) {
        final LocalTime nowTime = now.toLocalTime();
        LocalDate nowDate = now.toLocalDate();
        if (day == null) {
            if (nowTime.isAfter(startTime)) {
                nowDate = nowDate.plusDays(1L);
            }
            return nowDate.atTime(startTime);
        }
        return nowDate.atTime(startTime).with(nowTime.isAfter(startTime) ? TemporalAdjusters.next(day) : TemporalAdjusters.nextOrSame(day));
    }
    
    public void stop() {
        this.service.shutdownNow();
    }
    
    public static void set(final SchedulerManager instance) {
        SchedulerManager.instance = instance;
    }
    
    public static SchedulerManager get() {
        return SchedulerManager.instance;
    }
}
