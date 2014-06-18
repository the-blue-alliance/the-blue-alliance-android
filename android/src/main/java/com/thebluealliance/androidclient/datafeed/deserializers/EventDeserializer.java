package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Event;

import java.lang.reflect.Type;


public class EventDeserializer implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Event event = new Event();

        event.setEventKey(object.get("key").getAsString());
        event.setEventName(object.get("name").getAsString());
        // Location is null sometimes.
        if (object.get("location").isJsonNull()) {
            event.setLocation("");
        } else {
            event.setLocation(object.get("location").getAsString());
        }

        if (!object.has("venue_address") || object.get("venue_address").isJsonNull()){
            event.setVenue("");
        }
        else{
            event.setVenue(object.get("venue_address").getAsString());
        }
        event.setEventType(object.get("event_type").getAsInt());
        if (object.get("start_date").isJsonNull()) {
            event.setStartDate("");
        } else {
            event.setStartDate(object.get("start_date").getAsString());
        }
        if (object.get("end_date").isJsonNull()) {
            event.setEndDate("");
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

        if (object.has("website") && !object.get("website").isJsonNull()) {
            event.setWebsite(object.get("website").getAsString());
        }
        if (object.has("matches")) {
            event.setMatches(object.get("matches").getAsJsonArray());
        }
        if (object.has("webcast")) {
            event.setWebcasts(object.get("webcast").getAsJsonArray());
        }
        if (object.has("rankings")) {
            event.setRankings(object.get("rankings").getAsJsonArray());
        }
        if (object.has("stats")) {
            event.setStats(object.get("stats").getAsJsonObject());
        }
        if (object.has("alliances")) {
            event.setAlliances(object.get("alliances").getAsJsonArray());
        }
        if (object.has("event_district")) {
            JsonElement districtEnum = object.get("event_district");
            if (districtEnum.isJsonNull()) {
                event.setDistrictEnum(0);
            } else {
                event.setDistrictEnum(districtEnum.getAsInt());
            }
        } else {
            event.setDistrictEnum(0);
        }
        if (object.has("event_district_string")) {
            JsonElement districtString = object.get("event_district_string");
            if (districtString.isJsonNull()) {
                event.setDistrictTitle("");
            } else {
                String title = districtString.getAsString();
                event.setDistrictTitle(title.equals("null") ? "" : title);
            }
        } else {
            event.setDistrictTitle("");
        }

        return event;
    }
}
