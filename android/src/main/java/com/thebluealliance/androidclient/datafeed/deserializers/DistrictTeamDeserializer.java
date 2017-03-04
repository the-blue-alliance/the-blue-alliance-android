package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictRanking;

import java.lang.reflect.Type;
import java.util.List;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;

public class DistrictTeamDeserializer implements JsonDeserializer<DistrictRanking> {
    @Override
    public DistrictRanking deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final DistrictRanking districtTeam = new DistrictRanking();


        if (!isNull(object.get("team_key"))) {
            districtTeam.setTeamKey(object.get("team_key").getAsString());
        }

        if (!isNull(object.get("rank"))) {
            districtTeam.setRank(object.get("rank").getAsInt());
        }

        if (!isNull(object.get("rookie_bonus"))) {
            districtTeam.setRookieBonus(object.get("rookie_bonus").getAsInt());
        }

        if (!isNull(object.get("point_total"))) {
            districtTeam.setPointTotal(object.get("point_total").getAsInt());
        }

        if (!isNull(object.get("event_points"))) {
            districtTeam.setEventPoints(context.deserialize(object.get("event_points"),
                                                            new TypeToken<List<DistrictPointBreakdown>>(){}.getType()));
        }

        return districtTeam;
    }

    public static class DistrictEventPointsDeserializer implements JsonDeserializer<DistrictPointBreakdown>,
                                                                   JsonSerializer<DistrictPointBreakdown> {

        @Override
        public DistrictPointBreakdown deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject data = json.getAsJsonObject();
            DistrictPointBreakdown points = new DistrictPointBreakdown();

            if (!isNull(data.get("alliance_points"))) {
                points.setAlliancePoints(data.get("alliance_points").getAsInt());
            } else {
                points.setAlliancePoints(0);
            }

            if (!isNull(data.get("award_points"))) {
                points.setAwardPoints(data.get("award_points").getAsInt());
            } else {
                points.setAwardPoints(0);
            }

            if (!isNull(data.get("district_cmp"))) {
                points.setDistrictCmp(data.get("district_cmp").getAsBoolean());
            } else {
                points.setDistrictCmp(false);
            }

            if (!isNull(data.get("elim_points"))) {
                points.setElimPoints(data.get("elim_points").getAsInt());
            } else {
                points.setElimPoints(0);
            }

            if (!isNull(data.get("event_key"))) {
                points.setEventKey(data.get("event_key").getAsString());
            }

            if (!isNull(data.get("qual_points"))) {
                points.setQualPoints(data.get("qual_points").getAsInt());
            } else {
                points.setQualPoints(0);
            }

            if (!isNull(data.get("total"))) {
                points.setTotal(data.get("total").getAsInt());
            } else {
                points.setTotal(0);
            }

            return points;
        }

        @Override
        public JsonElement serialize(DistrictPointBreakdown src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject points = new JsonObject();

            points.addProperty("alliance_points", src.getAlliancePoints());
            points.addProperty("award_points", src.getAwardPoints());
            points.addProperty("district_cmp", src.getDistrictCmp());
            points.addProperty("elim_points", src.getElimPoints());
            points.addProperty("event_key", src.getEventKey());
            points.addProperty("qual_points", src.getQualPoints());
            points.addProperty("total", src.getTotal());
            points.addProperty("team_key", src.getTeamKey());
            return points;
        }

        private static boolean isNull(JsonElement data) {
            return data == null || data.isJsonNull();
        }
    }

}
