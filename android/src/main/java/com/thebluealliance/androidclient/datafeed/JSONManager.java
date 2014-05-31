package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.datafeed.deserializers.AwardDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.EventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.MediaDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.SimpleEventDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.SimpleTeamDeserializer;
import com.thebluealliance.androidclient.datafeed.deserializers.TeamDeserializer;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
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
            builder.registerTypeAdapter(SimpleEvent.class, new SimpleEventDeserializer());
            builder.registerTypeAdapter(SimpleTeam.class, new SimpleTeamDeserializer());
            builder.registerTypeAdapter(Team.class, new TeamDeserializer());
            builder.registerTypeAdapter(Media.class, new MediaDeserializer());
            gson = builder.create();
        }
        return gson;
    }

    public static JsonObject getasJsonObject(String input) {
        if (input == null || input.equals(""))
            return new JsonObject();
        JsonElement e = getParser().parse(input);
        if(e == null || e.isJsonNull()){
            return new JsonObject();
        }
        return e.getAsJsonObject();
    }

    public static JsonArray getasJsonArray(String input) {
        if (input == null || input.equals(""))
            return new JsonArray();
        return getParser().parse(input).getAsJsonArray();
    }
}
