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

    //the week of the year that each event starts competition on, starting with 1992
    public static final int[] FIRST_COMP_WEEK =
            {6, 8, 8, 7, 12, 9, 9, 8, 10, 8, 9, 9, 9, 9, 8, 8, 8, 8, 9, 9, 8, 8, 8};

    //the competition week of CMP that year, starting with 1992
    public static final int[] CMP_WEEK =
            {1, 1, 1, 6, 4, 6, 5, 9, 5, 6, 8, 6, 7, 8, 7, 7, 8, 8, 7, 9, 9, 9, 9};

}
