package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import rx.functions.Func1;

public class TeamStatsExtractor implements Func1<JsonElement, JsonElement> {

    private String mTeamKey;

    public TeamStatsExtractor(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public JsonElement call(JsonElement allStats) {
        if (allStats == null || !allStats.isJsonObject()) {
            return JsonNull.INSTANCE;
        }
        JsonObject allObject = allStats.getAsJsonObject();
        JsonObject teamStats = new JsonObject();
        if (allObject.has("oprs")) {
            JsonObject oprs = allObject.get("oprs").getAsJsonObject();
            if (oprs.has(mTeamKey)) {
                teamStats.addProperty("opr", oprs.get(mTeamKey).getAsDouble());
            }
        }
        if (allObject.has("dprs")) {
            JsonObject oprs = allObject.get("dprs").getAsJsonObject();
            if (oprs.has(mTeamKey)) {
                teamStats.addProperty("dpr", oprs.get(mTeamKey).getAsDouble());
            }
        }
        if (allObject.has("ccwms")) {
            JsonObject oprs = allObject.get("ccwms").getAsJsonObject();
            if (oprs.has(mTeamKey)) {
                teamStats.addProperty("ccwm", oprs.get(mTeamKey).getAsDouble());
            }
        }
        return teamStats;
    }
}
