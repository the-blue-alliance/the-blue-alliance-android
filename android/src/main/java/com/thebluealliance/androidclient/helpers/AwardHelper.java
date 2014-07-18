package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.Utilities;

/**
 * Created by phil on 6/24/14.
 */
public class AwardHelper {
    public static String buildWinnerString(String awardee, String team) {
        if (awardee.isEmpty()) {
            return "" + team;
        } else if (team.isEmpty()) {
            return awardee;
        } else {
            return awardee + " (" + team + ")";
        }
    }

    public static boolean validateAwardKey(String key) {
        String[] split = key.split(":");
        return  split.length == 2 &&
                EventHelper.validateEventKey(split[0]) &&
                Utilities.isInteger(split[1]);
    }

    public static String createAwardKey(String eventKey, int awardEnum){
        return eventKey + ":"+awardEnum;
    }
}
