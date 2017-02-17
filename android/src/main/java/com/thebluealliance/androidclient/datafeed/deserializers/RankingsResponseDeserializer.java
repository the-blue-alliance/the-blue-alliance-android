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

import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.RankingResponseObjectSortOrderInfo;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingResponseObjectSortOrderInfo;

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
        List<IRankingResponseObjectSortOrderInfo> sortOrders = new ArrayList<>();


        if (!isNull(rankingsObject.get("rankings"))) {
            JsonArray rankJson = rankingsObject.get("rankings").getAsJsonArray();
            for (int i = 0; i < rankJson.size(); i++) {
                teamRanks.add(context.deserialize(rankJson.get(i), RankingItem.class));
            }
        }

        if (!isNull(rankingsObject.get("sort_order_info"))) {
            JsonArray sortOrderJson = rankingsObject.get("sort_order_info").getAsJsonArray();
            for (int i = 0; i < sortOrderJson.size(); i++) {
                RankingResponseObjectSortOrderInfo column = new RankingResponseObjectSortOrderInfo();

                JsonObject sortItem = sortOrderJson.get(i).getAsJsonObject();
                column.setName(sortItem.get("name").getAsString());
                column.setPrecision(sortItem.get("precision").getAsInt());
                sortOrders.add(column);
            }
        }

        rankingResponse.setRankings(teamRanks);
        rankingResponse.setSortOrderInfo(sortOrders);
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

        JsonArray sortOrders = new JsonArray();
        for (int i = 0; i < src.getSortOrderInfo().size(); i++) {
            JsonObject sortOrder = new JsonObject();
            sortOrder.add("name", new JsonPrimitive(src.getSortOrderInfo().get(i).getName()));
            sortOrder.add("precision", new JsonPrimitive(src.getSortOrderInfo().get(i).getPrecision()));
            sortOrders.add(sortOrder);
        }
        data.add("sort_order_info", sortOrders);
        return data;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }
}
