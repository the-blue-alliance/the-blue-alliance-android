package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Match;

import java.lang.reflect.Type;


public class MatchDeserializer implements JsonDeserializer<Match>{

	@Override
	public Match deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final Match match = new Match();
		
		match.setKey(object.get("key").getAsString());
		match.setTypeFromShort(object.get("comp_level").getAsString());
		match.setMatchNumber(object.get("match_number").getAsInt());
		match.setSetNumber(object.get("set_number").getAsInt());
		match.setAlliances(object.get("alliances").getAsJsonObject());
		/* NOT YET EXPOSED TO THE API, uncomment below when it is
		 * match.setTime(object.get("time").getAsString());
		 * match.setVideos(object.get("videos").getAsJsonObject);
		 */
		match.setTime("");
		match.setVideos(new JsonObject());
		match.setLastUpdated(System.currentTimeMillis());
		
		return null;
	}

}
