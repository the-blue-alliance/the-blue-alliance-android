package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Award;

import java.lang.reflect.Type;


public class AwardDeserializer implements JsonDeserializer<Award>{
	
	@Override
	public Award deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final Award award = new Award();
		
		/*
		 * NOT AT ALL IMPLEMENT YET IN API
		 * Let's hold off here for now...
		 */
		
		return award;
	}

}
