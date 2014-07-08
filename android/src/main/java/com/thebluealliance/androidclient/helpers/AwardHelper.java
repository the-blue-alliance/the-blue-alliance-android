package com.thebluealliance.androidclient.helpers;

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
        //TODO put real key validation here
        return true;
    }
}
