package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.thebluealliance.androidclient.helpers.TeamHelper;

import rx.functions.Func1;

public class TeamRankExtractor implements Func1<JsonElement, JsonArray> {

    private String mTeamKey;

    public TeamRankExtractor(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public JsonArray call(JsonElement eventRanks) {
        if (eventRanks == null || !eventRanks.isJsonArray()) {
            return new JsonArray();
        }
        int teamNumber = TeamHelper.getTeamNumber(mTeamKey);

        JsonArray rankArray = eventRanks.getAsJsonArray();
        if (rankArray.size() <= 1) {
            return rankArray;
        }

        JsonArray headerRow = rankArray.get(0).getAsJsonArray();
        for (int i = 1; i < rankArray.size(); i++) {
            JsonArray rankRow = rankArray.get(i).getAsJsonArray();
            if (rankRow.get(1).getAsInt() == teamNumber) {
                JsonArray result = new JsonArray();
                result.add(headerRow);
                result.add(rankRow);
                return result;
            }
        }
        return new JsonArray();
    }
}
