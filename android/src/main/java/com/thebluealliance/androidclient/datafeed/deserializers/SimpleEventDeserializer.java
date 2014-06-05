package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.lang.reflect.Type;


public class SimpleEventDeserializer implements JsonDeserializer<SimpleEvent> {

    @Override
    public SimpleEvent deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final SimpleEvent event = new SimpleEvent();

        // Key and name shouldn't be null but doesn't hurt to check
        // in case something goes terribly wrong.
        if (object.get("key").isJsonNull()) {
            event.setEventKey("");
        } else {
            event.setEventKey(object.get("key").getAsString());
        }

        if (object.get("name").isJsonNull()) {
            event.setEventName("");
        } else {
            event.setEventName(object.get("name").getAsString());
        }
        // Location is null sometimes.
        if (object.get("location").isJsonNull()) {
            event.setLocation("");
        } else {
            event.setLocation(object.get("location").getAsString());
        }
        event.setEventType(object.get("event_type").getAsInt());
        event.setEventDistrict(""); /* NOT IMPLEMENTED IN API. Modify whenever it is... */

        // Start/End date is null sometimes (when spamming year changes)
        if (object.get("start_date").isJsonNull()) {
            event.setStartDate("1900-01-01");
        } else {
            event.setStartDate(object.get("start_date").getAsString());
        }

        if (object.get("end_date").isJsonNull()) {
            event.setEndDate("1900-01-02");
        } else {
            event.setEndDate(object.get("end_date").getAsString());
        }
        // For some reason "official" is sometimes null. Default to "false" in those cases
        if (object.get("official").isJsonNull()) {
            event.setOfficial(false);
        } else {
            event.setOfficial(object.get("official").getAsBoolean());
        }
        // "short_name" is not a required field in the API response.
        // If it is null, simply use the event name as the short name
        if (object.get("short_name").isJsonNull()) {
            event.setShortName("");
        } else {
            event.setShortName(object.get("short_name").getAsString());
        }
        event.setLastUpdated(System.currentTimeMillis());

        return event;
    }
}
