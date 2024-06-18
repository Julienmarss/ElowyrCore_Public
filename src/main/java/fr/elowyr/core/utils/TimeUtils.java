package fr.elowyr.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils
{
    public static String formatMS(final long ms) {
        final StringBuilder sb = new StringBuilder();
        final long timeInSeconds = ms / 1000L;
        final long days = timeInSeconds / 86400L;
        final long hours = timeInSeconds % 86400L / 3600L;
        final long minutes = timeInSeconds % 3600L / 60L;
        final long seconds = timeInSeconds % 60L;
        if (days > 0L) {
            sb.append(days).append('j');
        }
        if (days > 0L || hours > 0L) {
            sb.append(String.format("%02d", hours)).append('h');
        }
        if (days > 0L || hours > 0L || minutes > 0L) {
            sb.append(String.format("%02d", minutes)).append('m');
        }
        if (seconds > 0L) {
            sb.append(String.format("%02d", seconds)).append('s');
        }
        if (sb.length() == 0) {
            sb.append(ms).append("ms");
        }
        return sb.toString();
    }
    
    public static String format(final long timeInSeconds) {
        final StringBuilder sb = new StringBuilder();
        final long days = timeInSeconds / 86400L;
        final long hours = timeInSeconds % 86400L / 3600L;
        final long minutes = timeInSeconds % 3600L / 60L;
        final long seconds = timeInSeconds % 60L;
        if (days > 0L) {
            sb.append(days).append('j');
        }
        if (days > 0L || hours > 0L) {
            sb.append(String.format("%02d", hours)).append('h');
        }
        if (days > 0L || hours > 0L || minutes > 0L) {
            sb.append(String.format("%02d", minutes)).append('m');
        }
        sb.append(String.format("%02d", seconds)).append('s');
        return sb.toString();
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String timeToCheck) throws IllegalArgumentException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && timeToCheck.matches(reg)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date inTime = parseDate(dateFormat, initialTime);
            Date finTime = parseDate(dateFormat, finalTime);
            Date checkedTime = parseDate(dateFormat, timeToCheck);

            if (finalTime.compareTo(initialTime) < 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(finTime);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                finTime = calendar.getTime();
                if (timeToCheck.compareTo(initialTime) < 0) {
                    calendar.setTime(checkedTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    checkedTime = calendar.getTime();
                }
            }

            return (checkedTime.after(inTime) || checkedTime.compareTo(inTime) == 0) && checkedTime.before(finTime);
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
        }
    }

    /**
     * @param initialTime - in format HH:mm:ss
     * @param finalTime   - in format HH:mm:ss
     * @return initialTime <= now < finalTime
     * @throws IllegalArgumentException if passed date with wrong format
     */
    public static boolean isNowBetweenTwoTime(String initialTime, String finalTime) throws IllegalArgumentException {
        Date now = Calendar.getInstance().getTime();
        String format = new SimpleDateFormat("HH:mm:ss").format(now);
        return isTimeBetweenTwoTime(initialTime, finalTime, format);
    }

    private static Date parseDate(SimpleDateFormat dateFormat, String data) {
        try {
            return dateFormat.parse(data);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Not a valid time");
        }
    }

}
