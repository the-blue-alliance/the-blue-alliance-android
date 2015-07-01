package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonObject;

import rx.functions.Func1;

public class TeamStatsExtractor implements Func1<JsonObject, JsonObject> {

    private String mTeamKey;

    public TeamStatsExtractor(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public JsonObject call(JsonObject allStats) {
        JsonObject teamStats = new JsonObject();
        String teamNumber = mTeamKey.substring(3);
        if (allStats.has("oprs")) {
            JsonObject oprs = allStats.get("oprs").getAsJsonObject();
            if (oprs.has(teamNumber)) {
                teamStats.addProperty("opr", oprs.get(teamNumber).getAsDouble());
            }
        }
        if (allStats.has("dprs")) {
            JsonObject oprs = allStats.get("dprs").getAsJsonObject();
            if (oprs.has(teamNumber)) {
                teamStats.addProperty("dpr", oprs.get(teamNumber).getAsDouble());
            }
        }
        if (allStats.has("ccwms")) {
            JsonObject oprs = allStats.get("ccwms").getAsJsonObject();
            if (oprs.has(teamNumber)) {
                teamStats.addProperty("ccwm", oprs.get(teamNumber).getAsDouble());
            }
        }
        return teamStats;
    }
}
