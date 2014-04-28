package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.SimpleTeam;

import java.lang.reflect.Type;


public class SimpleTeamDeserializer implements JsonDeserializer<SimpleTeam>{

	@Override
	public SimpleTeam deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject object = json.getAsJsonObject();
		final SimpleTeam team = new SimpleTeam();
		
		team.setTeamKey(object.get("key").getAsString());
		team.setTeamNumber(object.get("team_number").getAsInt());
		team.setNickname(object.get("nickname").getAsString());
		team.setLocation(object.get("location").getAsString());
		team.setLastUpdated(System.currentTimeMillis());
		
		return team;
	}

}
