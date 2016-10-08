package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;

import java.lang.reflect.Type;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;


public class EventDeserializer implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object;
        try {
            object = json.getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException ex) {
            TbaLogger.w("Failed to parse json: " + json.toString());
            return null;
        }
        final Event event = new Event();

        if (object.has("key")) {
            String key = object.get("key").getAsString();
            int year = EventHelper.getYear(key);
            event.setKey(key);
            event.setYear(year);
        }

        if (object.has("name")) {
            event.setName(object.get("name").getAsString());
        }

        if (isNull(object.get("location"))) {
            event.setLocation("");
        } else {
            event.setLocation(object.get("location").getAsString());
        }

        if (isNull(object.get("venue_address"))) {
            event.setVenueAddress("");
        } else {
            event.setVenueAddress(object.get("venue_address").getAsString());
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
            event.setShortName("");
        } else {
            event.setShortName(object.get("short_name").getAsString());
        }

        if (!isNull(object.get("website"))) {
            event.setWebsite(object.get("website").getAsString());
        }

        if (object.has("webcast")) {
            event.setWebcasts(object.get("webcast").toString());
        }

        JsonElement districtEnum = object.get("event_district");
        if (isNull(districtEnum)) {
            event.setEventDistrict(0);
        } else {
            event.setEventDistrict(districtEnum.getAsInt());
        }

        JsonElement districtString = object.get("event_district_string");
        if (isNull(districtString)) {
            event.setEventDistrictString("");
        } else {
            String title = districtString.getAsString();
            event.setEventDistrictString(title.equals("null") ? "" : title);
        }

        JsonElement alliances = object.get("alliances");
        if (isNull(alliances)) {
            event.setAlliances("");
        } else {
            event.setAlliancesJson(alliances.getAsJsonArray());
        }

        return event;
    }
}
