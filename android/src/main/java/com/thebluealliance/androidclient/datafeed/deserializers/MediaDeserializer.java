package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.thebluealliance.androidclient.models.Media;

import java.lang.reflect.Type;

/**
 * File created by phil on 5/31/14.
 */
public class MediaDeserializer implements JsonDeserializer<Media> {
    @Override
    public Media deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Media media = new Media();

        if (object.has("type")) {
            media.setMediaType(object.get("type").getAsString());
        }

        if (object.has("foreign_key")) {
            media.setForeignKey(object.get("foreign_key").getAsString());
        } else if (object.has("key")) {
            //allow us to also deserialize medias coming from the match endpoint
            media.setForeignKey(object.get("key").getAsString());
        }

        if (object.has("details")) {
            media.setDetails(object.get("details").getAsJsonObject());
        }

        return media;
    }
}
