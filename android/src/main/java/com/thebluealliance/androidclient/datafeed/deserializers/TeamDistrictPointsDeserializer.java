package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.models.DistrictPointBreakdown;

import java.lang.reflect.Type;

/**
 * File created by phil on 7/26/14.
 */
public class TeamDistrictPointsDeserializer implements JsonDeserializer<DistrictPointBreakdown> {
    @Override
    public DistrictPointBreakdown deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        DistrictPointBreakdown breakdown = new DistrictPointBreakdown();

        if (object.has("qual_points")) {
            breakdown.setQualPoints(object.get("qual_points").getAsInt());
        }

        if (object.has("elim_points")) {
            breakdown.setElimPoints(object.get("elim_points").getAsInt());
        }

        if (object.has("alliance_points")) {
            breakdown.setAlliancePoints(object.get("alliance_points").getAsInt());
        }

        if (object.has("award_points")) {
            breakdown.setAwardPoints(object.get("award_points").getAsInt());
        }

        if (object.has("total")) {
            breakdown.setTotalPoints(object.get("total").getAsInt());
        }

        return breakdown;
    }
}
