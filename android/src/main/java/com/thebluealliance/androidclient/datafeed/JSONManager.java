package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.DistrictTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDistrictPointsDeserializer;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.DistrictPointBreakdown;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

/**
 * File created by phil on 4/22/14.
 */
public class JSONManager {
    private static Gson gson;
    private static JsonParser parser;

    public static JsonParser getParser() {
        if (parser == null)
            parser = new JsonParser();
        return parser;
    }

    public static Gson getGson() {
        if (gson == null) {
            /* Construct new gson with our custom deserializers */
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Award.class, new AwardDeserializer());
            builder.registerTypeAdapter(Event.class, new EventDeserializer());
            builder.registerTypeAdapter(Match.class, new MatchDeserializer());
            builder.registerTypeAdapter(Team.class, new TeamDeserializer());
            builder.registerTypeAdapter(Media.class, new MediaDeserializer());
            builder.registerTypeAdapter(DistrictTeam.class, new DistrictTeamDeserializer());
            builder.registerTypeAdapter(DistrictPointBreakdown.class, new TeamDistrictPointsDeserializer());
            gson = builder.create();
        }
        return gson;
    }

    public static JsonObject getasJsonObject(String input) {
        if (input == null || input.equals(""))
            return new JsonObject();
        JsonElement e = null;
        try {
            e = getParser().parse(input);
        }catch(JsonSyntaxException ex){
            Log.w(Constants.LOG_TAG, "Couldn't parse bad json: "+input);
        }
        if (e == null || e.isJsonNull()) {
            return new JsonObject();
        }
        try {
            return e.getAsJsonObject();
        } catch (IllegalStateException err) {
            Log.w(Constants.LOG_TAG, "getAsJsonObject failed: " + err);
            return new JsonObject();
        }
    }

    public static JsonArray getasJsonArray(String input) {
        if (input == null || input.equals(""))
            return new JsonArray();
        try {
            return getParser().parse(input).getAsJsonArray();
        } catch (IllegalStateException err) {
            Log.w(Constants.LOG_TAG, "getAsJsonArray failed: " + err);
            return new JsonArray();
        } catch (Exception ex){
            Log.w(Constants.LOG_TAG, "Attempted to parse invalid json");
            ex.printStackTrace();
            return new JsonArray();
        }

    }
}
