package com.thebluealliance.androidclient.datafeed.deserializers;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;

import java.lang.reflect.Type;


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

        if (isNull(object.get("address"))) {
            event.setAddress("");
        } else {
            event.setAddress(object.get("address").getAsString());
        }

        if (isNull(object.get("location_name"))) {
            event.setLocationName("");
        } else {
            event.setLocationName(object.get("location_name").getAsString());
        }

        if (isNull(object.get("city")) || isNull(object.get("state_prov")) || isNull(object.get("country"))) {
            event.setLocation("");
        } else {
            event.setLocation(object.get("city").getAsString() + ", " + object.get("state_prov").getAsString() + ", " + object.get("country").getAsString());
            event.setCity(object.get("city").getAsString());
        }

        if (object.has("event_type")) {
            event.setEventType(object.get("event_type").getAsInt());
        }

        if (isNull(object.get("start_date"))) {
            event.setStartDate("");
        } else {
            event.setStartDate(object.get("start_date").getAsString());
        }

        if (!isNull(object.get("week"))) {
            event.setWeek(object.get("week").getAsInt() + 1);
        } else {
            event.setCompetitionWeekFromStartDate();
        }

        if (isNull(object.get("end_date"))) {
            event.setEndDate("");
        } else {
            event.setEndDate(object.get("end_date").getAsString());
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

        if (object.has("webcasts")) {
            event.setWebcasts(object.get("webcasts").toString());
        }

        JsonElement district = object.get("district");
        if (isNull(district)) {
            event.setDistrict(null);
        } else {
            District districtModel = context.deserialize(object.get("district"), District.class);
            event.setDistrict(districtModel);
            event.setDistrictKey(districtModel.getKey());
        }

        return event;
    }
}
