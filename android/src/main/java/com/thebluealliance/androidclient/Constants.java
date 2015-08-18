package com.thebluealliance.androidclient;

import com.thebluealliance.androidclient.models.Media;

import java.util.HashMap;

/**
 * File created by phil on 4/22/14.
 */
public class Constants {
    public static final String LOG_TAG = "tba-android";
    public static final String DATAMANAGER_LOG = LOG_TAG + ":dataManager";
    public static final String REFRESH_LOG = LOG_TAG + ":refresh";

    // Keys for shared prefs
    // !!!!DO NOT MODIFY!!!!
    public static final String ALL_DATA_LOADED_KEY = "all_data_loaded";
    public static final String APP_VERSION_KEY = "app_version";

    public static final HashMap<String, String> MATCH_LEVELS;
    public static final HashMap<Media.TYPE, String> MEDIA_IMG_URL_PATTERN,
            MEDIA_LINK_URL_PATTERN;

    static {
        MATCH_LEVELS = new HashMap<>();
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

        MEDIA_IMG_URL_PATTERN = new HashMap<>();
        MEDIA_LINK_URL_PATTERN = new HashMap<>();
        MEDIA_IMG_URL_PATTERN.put(Media.TYPE.CD_PHOTO_THREAD, "http://www.chiefdelphi.com/media/img/%s");
        MEDIA_LINK_URL_PATTERN.put(Media.TYPE.CD_PHOTO_THREAD, "http://www.chiefdelphi.com/media/photos/%s");
        MEDIA_IMG_URL_PATTERN.put(Media.TYPE.YOUTUBE, "http://img.youtube.com/vi/%s/hqdefault.jpg");
        MEDIA_LINK_URL_PATTERN.put(Media.TYPE.YOUTUBE, "https://www.youtube.com/watch?v=%s");
    }

    public static String getApiHeader() {
        return "the-blue-alliance:android:v" + BuildConfig.VERSION_NAME.replace(":", "");  // X-TBA-App-Id must have exactly 3 semicolons
    }

    //the week of the year that each event starts competition on, starting with 1992
    //this is the nth week beginning in the given year
    public static final int[] FIRST_COMP_WEEK =
            {6, 8, 8, 7, 12, 9, 9, 8,               // 1992 - 1999
                    10, 8, 9, 9, 9, 9, 8, 8, 8, 8,  // 2000 - 2009
                    9, 9, 8, 8, 8, 8, 8};           // 2010 -

    //the competition week of CMP that year, starting with 1992
    public static final int[] CMP_WEEK =
            {1, 1, 1, 6, 4, 6, 5, 9,               // 1992 - 1999
                    5, 6, 8, 6, 7, 8, 7, 7, 8, 8,  // 2000 - 2009
                    7, 9, 9, 9, 9, 9, 9};          // 2010 -


    public static final int MAX_COMP_YEAR = 2016;
    public static final int FIRST_COMP_YEAR = 1992;
    public static final int FIRST_DISTRICT_YEAR = 2009;

    public static final int API_TEAM_LIST_PAGE_SIZE = 500;

    public static final long API_HIT_TIMEOUT_LONG = 60000;      // one minute in milliseconds
    public static final long API_HIT_TIMEOUT_SHORT = 500;        // five seconds in milliseconds
    public static final long MY_TBA_UPDATE_TIMEOUT = 600000;    // ten minutes in milliseconds
}
