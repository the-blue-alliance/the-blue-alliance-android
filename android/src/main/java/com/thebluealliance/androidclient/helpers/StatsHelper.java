package com.thebluealliance.androidclient.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class StatsHelper {

    private StatsHelper() {
        // unused
    }

    public static boolean canGenerateTopOprsString(JsonObject statsData) {
        return statsData != null
                && statsData.has("oprs")
                && statsData.get("oprs").isJsonObject();
    }

    public static String generateTopOprsString(JsonObject statsData, int itemLimit) {
        JsonObject oprs = statsData.get("oprs").getAsJsonObject();

        List<Pair<String, Double>> stats = new ArrayList<>();

        for (Map.Entry<String, JsonElement> stat : oprs.entrySet()) {
            String teamNumber = stat.getKey();
            double opr = stat.getValue().getAsDouble();

            stats.add(Pair.create(teamNumber, opr));
        }
        Collections.sort(stats, (lhs, rhs) -> {
            if(lhs.second > rhs.second) {
                return 1;
            } else if (lhs.second < rhs.second) {
                return -1;
            } else {
                return 0;
            }
        });

        String statsString = "";
        for (int i = 0; i < Math.min(itemLimit, stats.size()); i++) {
            String teamName = stats.get(i).first;
            String opr = ThreadSafeFormatters.formatDoubleTwoPlaces(stats.get(i).second);
            statsString += (i + 1) + ". " + teamName + " - <b>" + opr + "</b>";
            if (i < Math.min(itemLimit, stats.size()) - 1) {
                statsString += "<br>";
            }
        }
        return statsString.trim();
    }
}
