package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Award;

import java.lang.reflect.Type;


public class AwardDeserializer implements JsonDeserializer<Award> {

    @Override
    public Award deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject a = json.getAsJsonObject();
        final Award award = new Award();

        if(a.has("name")) {
            award.setName(a.get("name").getAsString());
        }

        if(a.has("event_key")) {
            award.setEventKey(a.get("event_key").getAsString());
        }

        if(a.has("year")) {
            award.setYear(a.get("year").getAsInt());
        }

        if(a.has("recipient_list")) {
            award.setWinners(a.get("recipient_list").getAsJsonArray());
        }

        return award;
    }

}
