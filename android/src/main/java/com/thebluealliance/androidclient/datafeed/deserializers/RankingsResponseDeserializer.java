package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.RankingSortOrder;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingSortOrder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RankingsResponseDeserializer implements JsonDeserializer<RankingResponseObject>,
                                                     JsonSerializer<RankingResponseObject> {

    @Override
    public RankingResponseObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject rankingsObject = json.getAsJsonObject();
        RankingResponseObject rankingResponse = new RankingResponseObject();

        List<IRankingItem> teamRanks = new ArrayList<>();
        List<IRankingSortOrder> sortOrders;
        List<IRankingSortOrder> extraStats;

        if (!isNull(rankingsObject.get("rankings"))) {
            JsonArray rankJson = rankingsObject.get("rankings").getAsJsonArray();
            for (int i = 0; i < rankJson.size(); i++) {
                teamRanks.add(context.deserialize(rankJson.get(i), RankingItem.class));
            }
        }

        if (!isNull(rankingsObject.get("sort_order_info"))) {
            JsonArray sortOrderJson = rankingsObject.get("sort_order_info").getAsJsonArray();
            sortOrders = context.deserialize(sortOrderJson,
                                             new TypeToken<List<RankingSortOrder>>(){}.getType());
        } else {
            sortOrders = new ArrayList<>();
        }

        if (!isNull(rankingsObject.get("extra_stats_info"))) {
            JsonArray extraStatsJson = rankingsObject.get("extra_stats_info").getAsJsonArray();
            extraStats = context.deserialize(extraStatsJson,
                                             new TypeToken<List<RankingSortOrder>>(){}.getType());
        } else {
            extraStats = new ArrayList<>();
        }

        rankingResponse.setRankings(teamRanks);
        rankingResponse.setSortOrderInfo(sortOrders);
        rankingResponse.setExtraStatsInfo(extraStats);
        return rankingResponse;
    }

    @Override
    public JsonElement serialize(RankingResponseObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject data = new JsonObject();

        JsonArray rankings = new JsonArray();
        for (int i = 0; i < src.getRankings().size(); i++) {
            rankings.add(context.serialize(src.getRankings().get(i), RankingItem.class));
        }
        data.add("rankings", rankings);

        data.add("sort_order_info", context.serialize(src.getSortOrderInfo(),
                                                      new TypeToken<List<RankingSortOrder>>(){}.getType()));

        data.add("extra_stats_info", context.serialize(src.getExtraStatsInfo(),
                                                       new TypeToken<List<RankingSortOrder>>(){}.getType()));
        return data;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    public static class RankingSortOrderDeserializer implements JsonDeserializer<RankingSortOrder>,
                                                                JsonSerializer<RankingSortOrder> {

        @Override
        public RankingSortOrder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject data = json.getAsJsonObject();
            RankingSortOrder column = new RankingSortOrder();

            column.setName(data.get("name").getAsString());
            column.setPrecision(data.get("precision").getAsInt());
            return column;
        }

        @Override
        public JsonElement serialize(RankingSortOrder src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject sortOrder = new JsonObject();
            sortOrder.add("name", new JsonPrimitive(src.getName()));
            sortOrder.add("precision", new JsonPrimitive(src.getPrecision()));
            return sortOrder;
        }
    }
}
