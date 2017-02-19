package com.thebluealliance.androidclient.helpers;

import android.text.TextUtils;

public final class EventTeamHelper {

    private EventTeamHelper() {
        // unused
    }

    public static String generateKey(String eventKey, String teamKey) {
        return eventKey + "_" + teamKey;
    }

    public static String getEventKey(String eventTeamKey) {
        return eventTeamKey.split("_")[0];
    }

    public static String getTeamKey(String eventTeamKey) {
        return eventTeamKey.split("_")[1];
    }

    public static boolean validateEventTeamKey(String key) {
        if (key == null || TextUtils.isEmpty(key)) {
            return false;
        }
        String[] split = key.split("_");
        return split.length == 2
                && EventHelper.validateEventKey(split[0])
                && (TeamHelper.validateTeamKey(split[1]) ^ TeamHelper.validateMultiTeamKey(split[1]));
    }
}
