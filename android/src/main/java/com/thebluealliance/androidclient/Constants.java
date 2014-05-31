package com.thebluealliance.androidclient;

import java.util.HashMap;

/**
 * File created by phil on 4/22/14.
 */
public class Constants {
    public static final String LOG_TAG = "com.thebluealliance.androidclient";

    public static final HashMap<String, String> MATCH_LEVELS;

    static {
        MATCH_LEVELS = new HashMap<String, String>();
        MATCH_LEVELS.put("Quals", "q");
        MATCH_LEVELS.put("q", "q");
        MATCH_LEVELS.put("qm", "q");
        MATCH_LEVELS.put("Qtr", "qf");
        MATCH_LEVELS.put("Quarters", "qf");
        MATCH_LEVELS.put("qf", "qf");
        MATCH_LEVELS.put("Semi", "sf");
        MATCH_LEVELS.put("Semis", "sf");
        MATCH_LEVELS.put("sf", "sf");
        MATCH_LEVELS.put("Finals", "f");
        MATCH_LEVELS.put("Final", "f");
        MATCH_LEVELS.put("f", "f");
    }

    public static String getApiHeader() {
        return "the-blue-alliance:android:v" + BuildConfig.VERSION_NAME;
    }

    public static final int FIRST_COMP_YEAR = 1992;
}
