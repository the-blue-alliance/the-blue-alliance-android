package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.Utilities;

public class AwardHelper {

    public static boolean validateAwardKey(String key) {
        if (key == null) return false;
        String[] split = key.split(":");
        return split.length == 2 &&
                EventHelper.validateEventKey(split[0]) &&
                Utilities.isInteger(split[1]);
    }

    public static String createAwardKey(String eventKey, int awardEnum) {
        return eventKey + ":" + awardEnum;
    }
}
