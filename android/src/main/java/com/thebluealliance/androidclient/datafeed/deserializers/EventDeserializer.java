package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Event;

import android.util.Log;

import java.lang.reflect.Type;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;


public class EventDeserializer implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object;
        try {
            object = json.getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException ex) {
            Log.w(Constants.LOG_TAG, "Failed to parse json: " + json.toString());
            return null;
        }
        final Event event = new Event();

        if (object.has("key")) {
            event.setEventKey(object.get("key").getAsString());
        }

        if (object.has("name")) {
            event.setEventName(object.get("name").getAsString());
        }

        if (isNull(object.get("location"))) {
            event.setLocation("");
        } else {
            event.setLocation(object.get("location").getAsString());
        }

        if (isNull(object.get("venue_address"))) {
            event.setVenue("");
        } else {
            event.setVenue(object.get("venue_address").getAsString());
        }

        if (object.has("event_type")) {
            event.setEventType(object.get("event_type").getAsInt());
        }

        if (isNull(object.get("start_date"))) {
            event.setStartDate("");
        } else {
            event.setStartDate(object.get("start_date").getAsString());
            event.setCompetitionWeekFromStartDate();
        }

        if (isNull(object.get("end_date"))) {
            event.setEndDate("");
        } else {
            event.setEndDate(object.get("end_date").getAsString());
        }

        if (isNull(object.get("official"))) {
            event.setOfficial(false);
        } else {
            event.setOfficial(object.get("official").getAsBoolean());
        }

        // "short_name" is not a required field in the API response.
        // If it is null, simply use the event name as the short name
        if (isNull(object.get("short_name"))) {
            event.setEventShortName("");
        } else {
            event.setEventShortName(object.get("short_name").getAsString());
        }

        if (!isNull(object.get("website"))) {
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

        JsonElement districtEnum = object.get("event_district");
        if (isNull(districtEnum)) {
            event.setDistrictEnum(0);
        } else {
            event.setDistrictEnum(districtEnum.getAsInt());
        }

        JsonElement districtString = object.get("event_district_string");
        if (isNull(districtString)) {
            event.setDistrictTitle("");
        } else {
            String title = districtString.getAsString();
            event.setDistrictTitle(title.equals("null") ? "" : title);
        }

        return event;
    }
}
