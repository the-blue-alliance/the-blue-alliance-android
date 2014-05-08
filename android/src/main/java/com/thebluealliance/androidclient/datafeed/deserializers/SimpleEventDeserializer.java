package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.lang.reflect.Type;


public class SimpleEventDeserializer implements JsonDeserializer<SimpleEvent>{
	
	@Override
	public SimpleEvent deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final SimpleEvent event = new SimpleEvent();
		
		event.setEventKey(object.get("key").getAsString());
		event.setEventName(object.get("name").getAsString());
		event.setLocation(object.get("location").getAsString());
		event.setEventType(object.get("event_type").getAsInt());
		event.setEventDistrict(""); /* NOT IMPLEMENTED IN API. Modify whenever it is... */
		event.setStartDate(object.get("start_date").getAsString());
		event.setEndDate(object.get("end_date").getAsString());
		event.setOfficial(object.get("official").getAsBoolean());
        event.setShortName(object.get("short_name").getAsString());
		event.setLastUpdated(System.currentTimeMillis());

		return event;
	}
}
