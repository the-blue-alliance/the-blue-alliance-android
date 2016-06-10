package com.thebluealliance.androidclient.helpers;

import java.text.DecimalFormat;

public final class StatsHelper {

    private StatsHelper() {
        // unused
    }

    private static DecimalFormat displayFormat = new DecimalFormat("#.##");

    public static String formatStat(double stat) {
        return displayFormat.format(stat);
    }

}
