package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.models.Team;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 4/7/15.
 */
public class TeamListDeserializer implements JsonDeserializer<List<Team>> {
    @Override
    public List<Team> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<Team> teams = new ArrayList<>();
        JsonArray data = json.getAsJsonArray();
        for (JsonElement aData : data) {
            teams.add(JSONManager.getGson().fromJson(aData, Team.class));
        }
        return teams;
    }
}
