package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import rx.functions.Func1;

public class TeamRankExtractor implements Func1<JsonArray, JsonArray> {

    private String mTeamKey;

    public TeamRankExtractor(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public JsonArray call(JsonArray eventRanks) {
        int teamNumber = TeamHelper.getTeamNumber(mTeamKey);

        if (eventRanks.size() <= 1) {
            return eventRanks;
        }

        JsonArray headerRow = eventRanks.get(0).getAsJsonArray();
        for (int i = 1; i < eventRanks.size(); i++) {
            JsonArray rankRow = eventRanks.get(i).getAsJsonArray();
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
