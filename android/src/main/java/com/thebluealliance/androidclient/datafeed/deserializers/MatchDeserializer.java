package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.models.Match;

import java.lang.reflect.Type;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;


public class MatchDeserializer implements JsonDeserializer<Match> {

    //used elsewhere, so define as constant
    public static final String ALLIANCE_TAG = "alliances";

    @Override
    public Match deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Match match = new Match();

        if (object.has("key")) {
            match.setKey(object.get("key").getAsString());
        }

        if (object.has("comp_level")) {
            match.setTypeFromShort(object.get("comp_level").getAsString());
        }

        if (object.has("match_number")) {
            match.setMatchNumber(object.get("match_number").getAsInt());
        }

        if (object.has("set_number")) {
            match.setSetNumber(object.get("set_number").getAsInt());
        }

        if (object.has(ALLIANCE_TAG)) {
            match.setAlliances(object.get(ALLIANCE_TAG).getAsJsonObject());
        }

        if (!isNull(object.get("time_string"))) {
            match.setTimeString(object.get("time_string").getAsString());
        }

        if (!isNull(object.get("time"))) {
            match.setTime(object.get("time").getAsLong());
        }

        if (object.has("videos")) {
            match.setVideos(object.get("videos").getAsJsonArray());
        }

        return match;
    }

}
