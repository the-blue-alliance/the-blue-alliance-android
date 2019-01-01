package com.thebluealliance.androidclient.views.breakdowns;

import android.view.View;

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

    static void setViewVisibility(View view, Boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    static String teamNumberFromKey(String teamKey) {
        return teamKey.substring(3);
    }

}
