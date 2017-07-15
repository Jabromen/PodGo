package me.bromen.podgo.extras.utilities;

import java.util.Locale;

/**
 * Created by jeff on 7/14/17.
 */

public class TimeUtils {

    private TimeUtils() {}

    public static String msecToHMS(int msec) {

        int hours = msec / 3600000;
        int remaining = msec % 3600000;

        int minutes = remaining / 60000;
        remaining = remaining % 60000;

        int seconds = remaining / 1000;

        return String.format(new Locale("en-US"), "%01d:%02d:%02d", hours, minutes, seconds);
    }
}
