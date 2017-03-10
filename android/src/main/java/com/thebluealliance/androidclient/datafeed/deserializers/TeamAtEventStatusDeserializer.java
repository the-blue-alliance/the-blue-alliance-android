package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.models.TeamAtEventStatus.TeamAtEventPlayoff;
import com.thebluealliance.api.model.IAllianceBackup;
import com.thebluealliance.api.model.IRankingSortOrder;
import com.thebluealliance.api.model.ITeamAtEventPlayoff;
import com.thebluealliance.api.model.ITeamRecord;

import java.lang.reflect.Type;
import java.util.List;

public class TeamAtEventStatusDeserializer implements JsonDeserializer<TeamAtEventStatus>,
                                                      JsonSerializer<TeamAtEventStatus> {
    @Override
    public TeamAtEventStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        TeamAtEventStatus status = new TeamAtEventStatus();

        if (!isNull(data.get("alliance"))) {
            JsonObject allianceJson = data.getAsJsonObject("alliance");
            TeamAtEventStatus.TeamAtEventAlliance alliance = new TeamAtEventStatus.TeamAtEventAlliance();

            alliance.setName(allianceJson.get("name").getAsString());
            alliance.setNumber(allianceJson.get("number").getAsInt());
            alliance.setPick(allianceJson.get("pick").getAsInt());
            if (!isNull(allianceJson.get("backup"))) {
                alliance.setBackup(context.deserialize(allianceJson.get("backup"), IAllianceBackup.class));
            }
            status.setAlliance(alliance);
        }

        if (!isNull(data.get("alliance_status_str"))) {
            status.setAllianceStatusStr(data.get("alliance_status_str").getAsString());
        }

        if (!isNull(data.get("overall_status_str"))) {
            status.setOverallStatusStr(data.get("overall_status_str").getAsString());
        }

        if (!isNull(data.get("playoff_status_str"))) {
            status.setPlayoffStatusStr(data.get("playoff_status_str").getAsString());
        }

        if (!isNull(data.get("playoff"))) {
            JsonObject playoffJson = data.getAsJsonObject("playoff");
            TeamAtEventPlayoff playoff = context.deserialize(playoffJson, TeamAtEventPlayoff.class);
            status.setPlayoff(playoff);
        }

        if (!isNull(data.get("qual"))) {
            JsonObject qualJson = data.getAsJsonObject("qual");
            TeamAtEventStatus.TeamAtEventQual qual = new TeamAtEventStatus.TeamAtEventQual();
            RankingItem teamRank = context.deserialize(qualJson.get("ranking"), RankingItem.class);
            List<IRankingSortOrder> sortOrders = context.deserialize(qualJson.get("sort_order_info"), new TypeToken<List<IRankingSortOrder>>(){}.getType());

            qual.setRanking(teamRank);
            qual.setSortOrderInfo(sortOrders);
            qual.setNumTeams(qualJson.get("num_teams").getAsInt());
            qual.setStatus(qualJson.get("status").getAsString());
            status.setQual(qual);
        }

        return status;
    }

    @Override
    public JsonElement serialize(TeamAtEventStatus src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject status = new JsonObject();

        status.addProperty("alliance_status_str", src.getAllianceStatusStr());
        status.addProperty("overall_status_str", src.getOverallStatusStr());
        status.addProperty("playoff_status_str", src.getPlayoffStatusStr());

        if (src.getAlliance() != null) {
            JsonObject allianceJson = new JsonObject();
            allianceJson.addProperty("name", src.getAlliance().getName());
            allianceJson.addProperty("number", src.getAlliance().getNumber());
            allianceJson.addProperty("pick", src.getAlliance().getPick());
            if (src.getAlliance().getBackup() != null) {
                allianceJson.add("backup", context.serialize(src.getAlliance().getBackup(),
                                                             EventAlliance.AllianceBackup.class));
            }
            status.add("alliance", allianceJson);
        } else {
            status.add("alliance", JsonNull.INSTANCE);
        }

        if (src.getPlayoff() != null) {
            status.add("playoff", context.serialize(src.getPlayoff(), ITeamAtEventPlayoff.class));
        } else {
            status.add("playoff", JsonNull.INSTANCE);
        }

        if (src.getQual() != null) {
            JsonObject qualJson = new JsonObject();

            if (src.getQual().getRanking() != null) {
                qualJson.add("ranking", context.serialize(src.getQual().getRanking(),
                                                          RankingItem.class));
            }

            if (src.getQual().getSortOrderInfo() != null) {
                qualJson.add("sort_order_info", context.serialize(src.getQual().getSortOrderInfo(),
                                                                  new TypeToken<List<IRankingSortOrder>>(){}.getType()));
            }

            qualJson.addProperty("num_teams", src.getQual().getNumTeams());
            qualJson.addProperty("status", src.getQual().getStatus());
            status.add("qual", qualJson);
        } else {
            status.add("qual", JsonNull.INSTANCE);
        }

        return status;
    }

    public static class Playoff implements JsonDeserializer<TeamAtEventPlayoff>,
                                           JsonSerializer<TeamAtEventPlayoff> {

        @Override
        public TeamAtEventPlayoff deserialize(JsonElement json, Type typeOfT,
                                                                JsonDeserializationContext context) throws JsonParseException {
            JsonObject playoffJson = json.getAsJsonObject();
            TeamAtEventPlayoff playoff = new TeamAtEventPlayoff();
            if (!isNull(playoffJson.get("current_level_record"))) {
                RankingItem.TeamRecord currentRecord = context
                        .deserialize(playoffJson.get("current_level_record"), ITeamRecord.class);
                playoff.setCurrentLevelRecord(currentRecord);
            }
            if (!isNull(playoffJson.get("record"))) {
                RankingItem.TeamRecord record = context
                        .deserialize(playoffJson.get("record"), ITeamRecord.class);
                playoff.setRecord(record);
            }

            if (!isNull(playoffJson.get("playoff_average"))) {
                playoff.setPlayoffAverage(playoffJson.get("playoff_average").getAsDouble());
            }

            playoff.setStatus(playoffJson.get("status").getAsString());
            playoff.setLevel(playoffJson.get("level").getAsString());
            return playoff;
        }

        @Override
        public JsonElement serialize(TeamAtEventPlayoff src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject playoffJson = new JsonObject();
            if (src.getCurrentLevelRecord() != null) {
                playoffJson.add("current_level_record", context.serialize(src.getCurrentLevelRecord(),
                                                                          ITeamRecord.class));
            }
            if (src.getRecord() != null) {
                playoffJson.add("record", context.serialize(src.getRecord(), ITeamRecord.class));
            }
            if (src.getPlayoffAverage() != null) {
                playoffJson.addProperty("playoff_average", src.getPlayoffAverage());
            }
            playoffJson.addProperty("level", src.getLevel());
            playoffJson.addProperty("status", src.getStatus());
            return playoffJson;
        }
    }

    private static boolean isNull(JsonElement obj) {
        return obj == null || obj.isJsonNull();
    }
}
