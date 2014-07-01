package com.thebluealliance.androidclient.datafeed.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.models.Team;

import java.lang.reflect.Type;


public class TeamDeserializer implements JsonDeserializer<Team> {

    @Override
    public Team deserialize(final JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final Team team = new Team();

        // All the teams should have an associated key and team number,
        // but it doesn't hurt to check in the rare case something goes terribly wrong.
        if (object.has("key") && !object.get("key").isJsonNull()) {
            team.setTeamKey(object.get("key").getAsString());
        }

        if (object.has("team_number") && !object.get("team_number").isJsonNull()) {
            team.setTeamNumber(object.get("team_number").getAsInt());
        }

        // Some of the old teams don't have names and/or locations.
        if (object.has("name") && !object.get("name").isJsonNull()) {
            team.setFullName(object.get("name").getAsString());
        }

        if (object.has("nickname") && !object.get("nickname").isJsonNull()) {
            team.setNickname(object.get("nickname").getAsString());
        }

        if (object.has("location") && !object.get("location").isJsonNull()) {
            team.setLocation(object.get("location").getAsString());
        }

        // Some teams don't have websites
        if (object.has("website") && !object.get("website").isJsonNull()) {
            team.setWebsite(object.get("website").getAsString());
        }

        return team;
    }

}
