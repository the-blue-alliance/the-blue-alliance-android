package com.thebluealliance.androidclient.views.breakdowns;

import com.google.gson.JsonObject;

public final class MatchBreakdownHelper {

    private MatchBreakdownHelper() {
        // Unused
    }

    static String getIntDefault(JsonObject data, String key) {
        if (data.has(key)) {
            return data.get(key).getAsString();
        } else {
            return "0";
        }
    }

    static int getIntDefaultValue(JsonObject data, String key) {
        if (data.has(key)) {
            return data.get(key).getAsInt();
        } else {
            return 0;
        }
    }

    static boolean getBooleanDefault(JsonObject data, String key) {
        return data.has(key) && data.get(key).getAsBoolean();
    }

    static String teamNumberFromKey(String teamKey) {
        return teamKey.substring(3);
    }

}
