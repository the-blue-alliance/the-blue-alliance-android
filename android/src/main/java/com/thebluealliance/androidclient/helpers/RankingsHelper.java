package com.thebluealliance.androidclient.helpers;

import com.google.gson.JsonArray;

public final class RankingsHelper {

    private RankingsHelper() {
        // unused
    }

    public static boolean canGenerateTopRanksString(JsonArray rankingsData) {
        return rankingsData != null && rankingsData.size() > 0;
    }

    public static String generateTopRanksString(JsonArray rankingsData, int itemLimit) {
        String rankString = "";
        if (rankingsData.size() <= 1) {
            return rankString;
        }
        for (int i = 1; i < Math.min(itemLimit + 1, rankingsData.size()); i++) {
            rankString += ((i) + ". <b>" + rankingsData.get(i).getAsJsonArray().get(1).getAsString()) + "</b>";
            if (i < Math.min(itemLimit + 1, rankingsData.size()) - 1) {
                rankString += "<br>";
            }
        }
        rankString = rankString.trim();
        return rankString;
    }
}
