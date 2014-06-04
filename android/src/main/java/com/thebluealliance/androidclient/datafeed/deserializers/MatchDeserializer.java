package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Match;

import java.lang.reflect.Type;


public class MatchDeserializer implements JsonDeserializer<Match> {

    //used elsewhere, so define as constant
    public static final String ALLIANCE_TAG = "alliances";

    @Override
    public Match deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Match match = new Match();

        match.setKey(object.get("key").getAsString());
        match.setTypeFromShort(object.get("comp_level").getAsString());
        match.setMatchNumber(object.get("match_number").getAsInt());
        match.setSetNumber(object.get("set_number").getAsInt());
        match.setAlliances(object.get(ALLIANCE_TAG).getAsJsonObject());
        if(object.has("time_string") && !object.get("time_string").isJsonNull()) {
            match.setTimeString(object.get("time_string").getAsString());
        }
        if(object.has("time") && !object.get("time").isJsonNull()) {
            match.setTime(object.get("time").getAsLong());
        }
        match.setVideos(object.get("videos").getAsJsonArray());
        match.setLastUpdated(System.currentTimeMillis());

        return match;
    }

}
