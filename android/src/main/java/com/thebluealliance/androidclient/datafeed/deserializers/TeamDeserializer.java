package com.thebluealliance.androidclient.datafeed.deserializers;

import static com.thebluealliance.androidclient.helpers.JSONHelper.isNull;

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
        if (!isNull(object.get("key"))) {
            team.setKey(object.get("key").getAsString());
        }

        if (!isNull(object.get("team_number"))) {
            team.setTeamNumber(object.get("team_number").getAsInt());
        }

        // Some of the old teams don't have names and/or locations.
        if (!isNull(object.get("name"))) {
            team.setName(object.get("name").getAsString());
        }

        if (!isNull(object.get("nickname"))) {
            team.setNickname(object.get("nickname").getAsString());
        }

        if (!isNull(object.get("location_name"))) {
            team.setLocationName(object.get("location_name").getAsString());
        }

        if (!isNull(object.get("address"))) {
            team.setAddress(object.get("address").getAsString());
        }

        if (!isNull(object.get("city")) && !isNull(object.get("state_prov")) && !isNull(object.get("country"))) {
            team.setLocation(object.get("city").getAsString() + ", " + object.get("state_prov").getAsString() + ", " + object.get("country").getAsString());
        }

        // Some teams don't have websites
        if (!isNull(object.get("website"))) {
            team.setWebsite(object.get("website").getAsString());
        }

        if (!isNull(object.get("motto"))) {
            team.setMotto(object.get("motto").getAsString());
        }

        return team;
    }

}
