package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.models.Event;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 4/7/15.
 */
public class EventListDeserializer implements JsonDeserializer<List<Event>> {
    @Override
    public List<Event> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<Event> events = new ArrayList<>();
        JsonArray data = json.getAsJsonArray();
        for (JsonElement aData : data) {
            events.add(JSONManager.getGson().fromJson(aData, Event.class));
        }
        return events;
    }
}
