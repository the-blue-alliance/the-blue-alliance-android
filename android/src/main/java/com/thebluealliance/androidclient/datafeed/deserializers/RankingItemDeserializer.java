package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.api.model.ITeamRecord;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RankingItemDeserializer implements JsonDeserializer<RankingItem>,
                                                JsonSerializer<RankingItem> {
    @Override
    public RankingItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        RankingItem rankItem = new RankingItem();

        rankItem.setDq(!isNull(data.get("dq")) ? data.get("dq").getAsInt() : 0);
        rankItem.setMatchesPlayed(data.get("matches_played").getAsInt());
        rankItem.setRank(data.get("rank").getAsInt());
        rankItem.setTeamKey(data.get("team_key").getAsString());
        if (!isNull(data.get("qual_average"))) {
            rankItem.setQualAverage(data.get("qual_average").getAsDouble());
        }

        if (!isNull(data.get("record"))) {
            rankItem.setRecord(context.deserialize(data.get("record"), ITeamRecord.class));
        }

        List<Double> sortOrders = new ArrayList<>();
        if (!isNull(data.get("sort_orders"))) {
            JsonArray sortOrderJson = data.get("sort_orders").getAsJsonArray();
            for (int i = 0; i < sortOrderJson.size(); i++) {
                sortOrders.add(sortOrderJson.get(i).getAsDouble());
            }
        }
        rankItem.setSortOrders(sortOrders);
        return rankItem;
    }

    @Override
    public JsonElement serialize(RankingItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject out = new JsonObject();
        out.add("dq", new JsonPrimitive(src.getDq()));
        out.add("matches_played", new JsonPrimitive(src.getMatchesPlayed()));
        out.add("rank", new JsonPrimitive(src.getRank()));
        out.add("team_key", new JsonPrimitive(src.getTeamKey()));
        if (src.getQualAverage() != null) {
            out.add("qual_average", new JsonPrimitive(src.getQualAverage()));
        } else {
            out.add("qual_average", JsonNull.INSTANCE);
        }

        if (src.getRecord() != null) {
            JsonElement record = context.serialize(src.getRecord(), RankingItem.TeamRecord.class);
            out.add("record", record);
        } else {
            out.add("record", JsonNull.INSTANCE);
        }

        JsonArray sortOrders = new JsonArray();
        for (int i = 0; i < src.getSortOrders().size(); i++) {
            sortOrders.add(src.getSortOrders().get(i));
        }
        out.add("sort_orders", sortOrders);
        return out;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    public static class RecordDeserializer implements JsonDeserializer<RankingItem.TeamRecord>,
                                                      JsonSerializer<RankingItem.TeamRecord> {

        @Override
        public RankingItem.TeamRecord deserialize(JsonElement json, Type typeOfT,
                                                  JsonDeserializationContext context) throws JsonParseException {
            JsonObject data = json.getAsJsonObject();
            RankingItem.TeamRecord record = new RankingItem.TeamRecord();

            record.setWins(data.get("wins").getAsInt());
            record.setLosses(data.get("losses").getAsInt());
            record.setTies(data.get("ties").getAsInt());
            return record;
        }

        @Override
        public JsonElement serialize(RankingItem.TeamRecord src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject data = new JsonObject();
            data.addProperty("wins", src.getWins());
            data.addProperty("losses", src.getLosses());
            data.addProperty("ties", src.getTies());
            return data;
        }
    }
}
