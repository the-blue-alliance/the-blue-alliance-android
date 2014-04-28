package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Event;

import java.lang.reflect.Type;


public class EventDeserializer implements JsonDeserializer<Event>{

	@Override
	public Event deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final Event event = new Event();
		
		/* TODO there should be a way to use inheritance to do this section with SimpleEventDeserializer
		 * But I'm not sure how to do it without constructing a dummy SimpleTeam.
		 * There's probably a way, but it isn't coming to me at the moment...
		 */
		event.setEventKey(object.get("key").getAsString());
		event.setEventName(object.get("name").getAsString());
		event.setLocation(object.get("location").getAsString());
		event.setEventType(object.get("event_type").getAsInt());
		event.setEventDistrict(""); /* NOT IMPLEMENTED IN API. Modify whenever it is... */
		event.setStartDate(object.get("start_date").getAsString());
		event.setEndDate(object.get("end_date").getAsString());
		event.setOfficial(object.get("official").getAsBoolean());
		event.setLastUpdated(System.currentTimeMillis());
		
		//event.setWebsite(""); /* NOT EXPOSED BY API YET */
		if(object.has("matches")) {
			event.setMatches(object.get("matches").getAsJsonArray());
		}
		if(object.has("webcasts")) {
			//event.setWebcasts(); /* NOT EXPOSED BY API YET */
		}
		if(object.has("rankings")) {
			//event.setRankings(); /* NOT EXPOSED BY API YET */
		}
		if(object.has("stats")) {
			event.setStats(object.get("stats").getAsJsonObject());
		}
		
		return event;
	}
}
