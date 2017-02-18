package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.thebluealliance.androidclient.models.Match;

import java.lang.reflect.Type;

public class MatchVideoDeserializer implements JsonDeserializer<Match.MatchVideo>,
                                               JsonSerializer<Match.MatchVideo> {
    @Override
    public Match.MatchVideo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        Match.MatchVideo video = new Match.MatchVideo();
        video.setKey(data.get("key").getAsString());
        video.setType(data.get("type").getAsString());
        return video;
    }

    @Override
    public JsonElement serialize(Match.MatchVideo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject video = new JsonObject();
        video.addProperty("key", src.getKey());
        video.addProperty("type", src.getType());
        return video;
    }
}
